package il.co.ilrd.gatewayserver;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.json.JSONException;
import org.json.JSONObject;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import il.co.ilrd.observer.Callback;
import il.co.ilrd.observer.Dispatcher;


public class GatewayServer {
	private ThreadPoolExecutor threadPool;
	private MessageHandler messageHandler = new MessageHandler();
	private ConnectionsHandler connectionsHandler = new ConnectionsHandler();
	private CMDFactory<FactoryCommand, String, Object> cmdFactory = CMDFactory.getFactoryInstance();
	private static Map<String, DatabaseManagementServer> companiesMap = new HashMap<>();
	private boolean isServerRunning = false;
	private static final int MAX_NUM_THREADS = 10;
	private static final String DB_NAME = "dbName";
	private static final String URL = "jdbc:mysql://localhost";
	private static final String USER_NAME = "root";
	private static final String USER_PASSWORD = "123456"; 
	private static final String JAR_EXTENSTION = ".jar";
	private static String interfaceName;
	private String dirOfJarsPath = "C:\\jars";
	private JarHandler jarHandler = new JarHandler();
	private FactoryCommandLoader factoryCommandLoader = new FactoryCommandLoader();

	public GatewayServer(int numOfThreads, String interfaceName) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, IOException {
		threadPool = new ThreadPoolExecutor(numOfThreads, MAX_NUM_THREADS, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		GatewayServer.interfaceName = interfaceName;
		initCMDFactory();
	}

	public GatewayServer(String interfaceName) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, IOException {
		this(Runtime.getRuntime().availableProcessors(), interfaceName);
	}
	
	public void addLowHttpServer(ServerPort port) throws Exception {
		IsServerRunning();
		int portNumber = port.getPort();
		isPortNumberIsValid(portNumber);

		ServerConnection httpConnection = new LowHttpServer(port);
		connectionsHandler.addConnection(httpConnection, connectionsHandler.tcpPortsInUse, portNumber);
	}
	
	public void addHighHttpServer(ServerPort port) throws Exception {
		IsServerRunning();
		int portNumber = port.getPort();
		isPortNumberIsValid(portNumber);
		
		HighHttpServer highHttpServer= new HighHttpServer(portNumber);
		connectionsHandler.addConnection(highHttpServer, connectionsHandler.tcpPortsInUse, portNumber);
	}
	
	public void addTcpServer(ServerPort port) throws IOException, Exception {
		IsServerRunning();
		int portNumber = port.getPort();
		isPortNumberIsValid(portNumber);
		
		connectionsHandler.addConnection(new TcpConnection(portNumber), connectionsHandler.tcpPortsInUse, portNumber);
	}
	
	public void addUdpServer(ServerPort port) throws IOException, Exception {
		IsServerRunning();
		int portNumber = port.getPort();
		isPortNumberIsValid(portNumber);

		connectionsHandler.addConnection(new UdpConnection(portNumber), connectionsHandler.udpPortsInUse, portNumber );
	}
	
	public void start() {
		isServerRunning = true;
		connectionsHandler.startConnections();
		jarHandler.startJarHandler();
	}
	
	public void stop() throws IOException {
		isServerRunning = false;
		connectionsHandler.stopConnections();
		threadPool.shutdown();
		jarHandler.stopJarHandler();
	}
	
	public void setNumOfThreads(int numOfThread) {
		threadPool.setCorePoolSize(numOfThread);
	}
	
	private void initCMDFactory() throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, IOException {
		jarHandler.loadJars();
	}

	private boolean isJarFile(File file) {
		return file.isFile() && file.getName().endsWith(JAR_EXTENSTION);
	}

	private void IsServerRunning() throws Exception {
		if(isServerRunning) {
			throw new Exception("Not possible to add connection becuase server is running");
		}
	}

	private void isPortNumberIsValid(int portNumber) throws Exception {
		for(ServerConnection connection : connectionsHandler.connectionsList) {
			if(portNumber == connection.getPortNumber()) {
				throw new Exception("Not possible to add connection becuase port number not available");
			}
		}
	}
	
	private String getStringFromBuffer(ByteBuffer buffer) throws UnsupportedEncodingException {
        return new String(buffer.array(), "UTF-8");
	}
	
	/**********************************************
	 * Connections Handler
	 **********************************************/
	private class ConnectionsHandler implements Runnable {
		private Selector selector;
		private List<Integer> tcpPortsInUse = new ArrayList<>();
		private List<Integer> udpPortsInUse = new ArrayList<>();
		private List<ServerConnection> connectionsList = new ArrayList<>();
		private Map<Channel, ServerConnection> mapChannelConnection = new HashMap<>();
		private static final int BUF_SIZE = 2048;
		private static final int TIMEOUT = 5000;
		
		private void startConnections() {
			try {
				selector = Selector.open();
			} catch (IOException e) {
				e.printStackTrace();
			}
			new Thread(this).start();
		}
		
		@Override
		public void run() {
			ByteBuffer messageBuffer = ByteBuffer.allocate(BUF_SIZE);
			
			try{
				for(ServerConnection connection : connectionsList) {
					connection.initConnection(selector);
					mapChannelConnection.put(connection.getChannel(), connection);
				}

				while (isServerRunning) {
					if(0 == selector.select(TIMEOUT)) {
						if(isServerRunning) {
							System.out.println("waiting for connection");						
							continue;
						}
					}

					Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
					while (iter.hasNext()) {
						SelectionKey key = iter.next();
						Channel currentChannel = key.channel();
						
						if(!key.isValid()) {
							continue;
						}
						if (key.isAcceptable()) {
							registerTcpClientToSelector(currentChannel);
						}
						if (key.isReadable()) {
							ServerConnection currentConnection = mapChannelConnection.get(currentChannel);
							currentConnection.receiveMessage(currentChannel, messageBuffer);
						}
						iter.remove();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
	
		private void registerTcpClientToSelector(Channel currentChannel) throws IOException {
			ServerSocketChannel tcpServer = (ServerSocketChannel)currentChannel;
			SocketChannel channel = tcpServer.accept();
			channel.configureBlocking(false);
			channel.register(selector, SelectionKey.OP_READ);
			mapChannelConnection.put(channel, mapChannelConnection.get(currentChannel));
		}
		
		private void stopConnections() throws IOException {
			System.out.println("Closing the server");
			closeChannels();
			selector.close();
		}
		
		private void closeChannels() throws IOException {
			for (ServerConnection serverConnection : connectionsList) {
				serverConnection.stopServer();
			}
		}

		private void addConnection(ServerConnection connection, List<Integer> portsInUse, int portNumber) {
			connectionsList.add(connection);
			portsInUse.add(portNumber);
		}
	}
	
	/***********************************************
	 * Message Handler
	 **********************************************/
	private class MessageHandler {
		private JsonToRunnableConvertor jsonToRunnableConvertor = new JsonToRunnableConvertor();
		private String jsonString;
		private Runnable runnable;
		
		private void handleMessage(ByteBuffer buffer, ClientInfo clientInfo) throws IOException, JSONException {
			jsonString = getStringFromBuffer(buffer);
			buffer.clear();
			handleMessage(jsonString, clientInfo);
		}

		public void handleMessage(String bodyString, ClientInfo clientInfo) throws JSONException {
			runnable = jsonToRunnableConvertor.convertToRunnable(bodyString, clientInfo);
			threadPool.submit(runnable);
		}
	}
	
	/***********************************************
	 * JsonToRunnableConvertor
	 **********************************************/
	private class JsonToRunnableConvertor {
		private final static String COMMAND_KEY = "CommandKey";
		private final static String DATA = "Data";
		private String stringCommandkey;
		private JSONObject data;

		private Runnable convertToRunnable(String jsonMessage, ClientInfo clientInfo) throws JSONException {
			stringCommandkey = getFactoryCommandKey(jsonMessage);
			data = getFactoryData(jsonMessage);
			
			return new Runnable() {
				@Override
				public void run() {
					String responseMessage = "";
					if(null != stringCommandkey) {
						try {
							String dbName = data.getString(DB_NAME);
							createDBIfNotExist(dbName);
							DatabaseManagementServer DbManagemaent = companiesMap.get(dbName);
							if(cmdFactory.contains(stringCommandkey)) {
								responseMessage = cmdFactory.create(stringCommandkey).run(data, DbManagemaent);
							}else {
								responseMessage = "Error - command not exist";
							}
						} catch (JSONException | SQLException e) {
							responseMessage = "Error - " + e.getMessage();
						}
					} else {
						responseMessage = "Error - no command key ";
					}
					sendResponseMessage(clientInfo, responseMessage);
				}
			};
		}

		private String getFactoryCommandKey(String message) throws JSONException {
			JSONObject jsonObject = new JSONObject(message);
			return jsonObject.getString(COMMAND_KEY);
		}
		
		private JSONObject getFactoryData(String message) throws JSONException {
			JSONObject jsonObject = new JSONObject(message);
			return jsonObject.getJSONObject(DATA);
		}
	}
	
	/**********************************************
	 * Connection Interface
	 **********************************************/
	private interface ServerConnection {
		public void sendMessage(ClientInfo clientInfo, ByteBuffer message) throws IOException;
		public void stopServer() throws IOException;
		public void receiveMessage(Channel channel, ByteBuffer messageBuffer) throws IOException, JSONException;
		public void initConnection(Selector selector) throws UnknownHostException, IOException;
		public Channel getChannel();
		public int getPortNumber();
	}

	/**********************************************
	 * TCP Connection
	 **********************************************/
	private class TcpConnection implements ServerConnection {
		private int portNumber;
		private ServerSocketChannel TcpServerChannel;

		public TcpConnection(int portNumber) throws IOException {
			this.portNumber = portNumber;
			TcpServerChannel = ServerSocketChannel.open();
		}

		@Override
		public void initConnection(Selector selector) throws UnknownHostException, IOException {
			TcpServerChannel.bind(new InetSocketAddress(portNumber));
			TcpServerChannel.configureBlocking(false);
			TcpServerChannel.register(selector, SelectionKey.OP_ACCEPT);
		}
		
		@Override
		public Channel getChannel() {
			return TcpServerChannel;
		}

		@Override
		public void sendMessage(ClientInfo clientInfo, ByteBuffer message) {
			try {
				while(message.hasRemaining()) {
					System.out.println("message = " + getStringFromBuffer(message));
					(clientInfo.tcpSocketChannel).write(message);	
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				message.clear();				
			}
		}

		@Override
		public void receiveMessage(Channel channel, ByteBuffer messageBuffer) throws IOException, JSONException {
			SocketChannel clientChannel = (SocketChannel) channel;
			if (-1 == clientChannel.read(messageBuffer)) {
				clientChannel.close();
				System.out.println("Client closed the connection");
			}
			else {
				ClientInfo clientInfo = new ClientInfo(clientChannel, this);
				messageHandler.handleMessage(messageBuffer, clientInfo);
				messageBuffer.clear();
			}
		}

		@Override
		public int getPortNumber() {
			return portNumber;
		}

		@Override
		public void stopServer() throws IOException {
			TcpServerChannel.close();
		}
	}

	/**********************************************
	 * UDP Connection
	 **********************************************/
	private class UdpConnection implements ServerConnection {
		private int portNumber;
		private DatagramChannel udpServer;
		
		private UdpConnection(int portNumber) throws IOException {
			this.portNumber = portNumber;
			udpServer = DatagramChannel.open();
		}

		@Override
		public void initConnection(Selector selector) throws IOException {
			udpServer.socket().bind(new InetSocketAddress(portNumber));
			udpServer.configureBlocking(false);
			udpServer.register(selector, SelectionKey.OP_READ);	
		}

		@Override
		public Channel getChannel() {
			return udpServer;
		}

		@Override
		public void sendMessage(ClientInfo clientInfo, ByteBuffer message) {
			try {
				udpServer.send(message, clientInfo.udpSocketAddress);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void receiveMessage(Channel channel, ByteBuffer messageBuffer) throws IOException, JSONException {
			DatagramChannel datagramChannel = (DatagramChannel)channel;
			SocketAddress clientAddress = datagramChannel.receive(messageBuffer);
			ClientInfo clientInfo = new ClientInfo(clientAddress, this);
			messageHandler.handleMessage(messageBuffer, clientInfo);
			messageBuffer.clear();
		}

		@Override
		public int getPortNumber() {
			return portNumber;
		}

		@Override
		public void stopServer() throws IOException {
			udpServer.close();
		}
	}
	
	/**********************************************
	 * Low Http Server
	 **********************************************/
	public class LowHttpServer implements ServerConnection {
		private int portNumber;
		private HttpVersion httpVersion;
		private ServerSocketChannel httpServerSocket;
		private HTTPMessageParser httpMessageParser;
		private HashMap<String, String> responseHeadersMap = new HashMap<>();

		public LowHttpServer(ServerPort port) throws IOException {
			this.portNumber = port.getPort();
		}
		
		@Override
		public void sendMessage(ClientInfo clientInfo, ByteBuffer message) throws IOException {
			HttpStatusCode httpStatusCode = HttpStatusCode.OK;
			String stringMessage = getStringFromBuffer(message);
			initResponseHeadersMap(stringMessage.length());
			String response = HttpBuilder.createHttpResponseMessage(httpVersion, httpStatusCode, responseHeadersMap, stringMessage);
			try {
				ByteBuffer responseBuffer = Charset.forName("ASCII").encode(response);
				while(responseBuffer.hasRemaining()) {
					(clientInfo.tcpSocketChannel).write(responseBuffer);	
				}	
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void receiveMessage(Channel channel, ByteBuffer messageBuffer) throws IOException, JSONException {
			SocketChannel clientChannel = (SocketChannel) channel;
			if (-1 == clientChannel.read(messageBuffer)) {
				clientChannel.close();
				System.out.println("Client closed the connection");
			}
			else {
				ClientInfo clientInfo = new ClientInfo(clientChannel, this);
				httpMessageParser = new HTTPMessageParser(getStringFromBuffer(messageBuffer));
				httpVersion = httpMessageParser.getStartLine().getHttpVersion();
				messageHandler.handleMessage(httpMessageParser.getBody().getBodyText(), clientInfo);
				messageBuffer.clear();
			}
		}
		
		@Override
		public void initConnection(Selector selector) throws UnknownHostException, IOException {
			httpServerSocket = ServerSocketChannel.open();
			httpServerSocket.bind(new InetSocketAddress(portNumber));
			httpServerSocket.configureBlocking(false);
			httpServerSocket.register(selector, SelectionKey.OP_ACCEPT);
		}

		@Override
		public Channel getChannel() {
			return httpServerSocket;
		}

		@Override
		public int getPortNumber() {
			return portNumber;
		}
		
		private void initResponseHeadersMap(int length) {
			responseHeadersMap.put("Content-Length", String.valueOf(length));
			responseHeadersMap.put("Connection", "close");			
			responseHeadersMap.put("Content-Type", "application/json");			
		}

		@Override
		public void stopServer() throws IOException {
			httpServerSocket.close();			
		}
	}
	
	/**********************************************
	 * High Sun Http Server
	 **********************************************/
	public class HighHttpServer implements ServerConnection {
		private HttpServer httpServer;
		private Headers headers;
		private int portNumber;
		private final static String ROOT = "/";
		private final static int DELAY_TIMEOUT = 0;

		public HighHttpServer(int portNumber) throws IOException {
			this.portNumber = portNumber;
			httpServer = HttpServer.create(new InetSocketAddress(portNumber), 0);
			addContexts();
		}

		private void addContexts() {
			httpServer.createContext(ROOT, new RootHandler());
		}
		
		public void start() {
			httpServer.start();
		}
		
		@Override
		public void stopServer() throws IOException {
			httpServer.stop(DELAY_TIMEOUT);			
		}
		
		@Override
		public void initConnection(Selector selector) throws UnknownHostException, IOException {
			start();
		}

		@Override
		public int getPortNumber() {
			return portNumber;
		}
	
		@Override
		public void sendMessage(ClientInfo clientInfo, ByteBuffer bufferMessage) throws IOException {
			String stringMessage = getStringFromBuffer(bufferMessage);
			HttpStatusCode statusCode = HttpStatusCode.OK;
			HttpExchange httpExchange = clientInfo.getHttpExchange();
			fillHeadersWithStandardHeaders(httpExchange);
			httpExchange.sendResponseHeaders(statusCode.getCode(), stringMessage.length());
			try(OutputStream os = httpExchange.getResponseBody();){
				os.write(stringMessage.getBytes());
			}
		}

		private void fillHeadersWithStandardHeaders(HttpExchange httpExchange) {
			headers = httpExchange.getResponseHeaders();
			headers.add("Content-Type", "application/json");
			headers.add("Connection", "closed");
		}
			
		private class RootHandler implements HttpHandler {
			@Override
			public void handle(HttpExchange httpExchange) throws IOException {
				ClientInfo clientInfo = new ClientInfo(httpExchange, HighHttpServer.this);
				try {
					messageHandler.handleMessage(getBodyAsString(httpExchange), clientInfo);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		
		private String getBodyAsString(HttpExchange httpExchange) throws IOException {
			byte[] byteArray = new byte[4096];
			try(InputStream inputStream = httpExchange.getRequestBody();) {
				inputStream.read(byteArray);
				return new String(byteArray).trim();
			}
		}
	
		@Override
		public void receiveMessage(Channel channel, ByteBuffer messageBuffer) throws IOException, JSONException {
			//Not implemented
		}
		
		@Override
		public Channel getChannel() {
			//Not implemented
			return null;
		}
	}
	
	/**********************************************
	 * Request Parser 
	 **********************************************/
	public class RequestParser {
		private Map<String, String> queryStringMap = new HashMap<>();
		private static final String FORWARD_SLASH = "[/]";
		private static final String AMPERSAND = "[&]";
		private static final String EQUAL = "[=]";
		private static final int KEY_ARG = 0;
		private static final int VALUE_ARG = 1;
		private static final int COMPANY_NAME_ARG = 2;
		private static final int TABLE_NAME_ARG = 3;
		private static final int LIMIT = 2;
		private String companyName;
		private String tableName;
		
		public RequestParser(URI uri, InputStream inputStream) throws IOException, JSONException {
			parsePath(uri.getPath());
			parseQueryString(uri.getQuery());
		}				
		
		private void parsePath(String path) {
			String[] requestTokens = path.split(FORWARD_SLASH);			
			companyName = requestTokens[COMPANY_NAME_ARG];
			tableName = requestTokens[TABLE_NAME_ARG];
		}
		
		private void parseQueryString(String query) {
			if(null != query) {
				String[] parameters = query.split(AMPERSAND);	
				String[] row;
				for(String parameter : parameters) {
					row = parameter.split(EQUAL, LIMIT);
					queryStringMap.put(row[KEY_ARG], row[VALUE_ARG]);
				}					
			}
		}
		
		private Map<String, String> getQueryStringMap() {
			return queryStringMap;
		}
		
		private String getCompanyName() {
			return companyName;
		}
		
		private String getTableName() {
			return tableName;
		}
	}
	
	/**********************************************
	 * Client Info 
	 **********************************************/
	public class ClientInfo {
		private SocketChannel tcpSocketChannel = null;
		private SocketAddress udpSocketAddress = null;
		private ServerConnection connection = null;
		private HttpExchange httpExchange = null;
		
		public ClientInfo(SocketChannel tcpSocketChannel, ServerConnection connection) {
			this.tcpSocketChannel = tcpSocketChannel;
			this.connection = connection;
		}
		
		public ClientInfo(SocketAddress udpSocketAddress, ServerConnection connection) {
			this.udpSocketAddress = udpSocketAddress;
			this.connection = connection;
		}
		
		public ClientInfo(HttpExchange httpExchange, ServerConnection connection) {
			this.httpExchange = httpExchange;
			this.connection = connection;
		}
		
		public SocketChannel getTcpSocketChannel() {
			return tcpSocketChannel;
		}
		
		public SocketAddress getUdpSocketAddress() {
			return udpSocketAddress;
		}
		
		public HttpExchange getHttpExchange() {
			return httpExchange;
		}
		
		public ServerConnection getConnection() {
			return connection;
		}
	}

	/**********************************************
	 * Factory Command Loader 
	 **********************************************/
	private class FactoryCommandLoader {
		private final static String ADD_TO_FACTORY_METHOD = "addToFactory";
		private final static String GET_VERSION_METHOD = "getVersion";
		private Map<String, Double> versionsMap = new HashMap<>();

		private void load(String jarFilePath) throws ClassNotFoundException, IOException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
			for(Class<?> classIter : JarLoader.load(interfaceName, jarFilePath)) {
				Double newClassVersion = (Double)classIter.getMethod(GET_VERSION_METHOD).invoke(null);
				
				if(!isClassInVersionMap(classIter) || isClassUpdated(classIter, newClassVersion)) {
					addToMap(classIter, newClassVersion);
					invokeMethod(classIter);					
				}
			}
		}
		
		private boolean isClassUpdated(Class<?> class1, Double newClassVersion) {
			try {
				return versionsMap.get(class1.getName()) < newClassVersion;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
			return false;
		}

		private boolean isClassInVersionMap(Class<?> class1) {
			return versionsMap.containsKey(class1.toString());
		}

		private void addToMap(Class<?> class1, Double newClassVersion) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
			versionsMap.put(class1.getName(), newClassVersion);
		}
		
		private void invokeMethod(Class<?> class1) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, NoSuchMethodException, SecurityException {
			Method method = class1.getMethod(ADD_TO_FACTORY_METHOD);
			method.invoke(class1.getConstructor().newInstance());
		}
	}
	
	/**********************************************
	 * Jar Handler 
	 **********************************************/
	private class JarHandler {
		private JarMonitor jarMonitor;
		private JarDirectoryAnalyzer jarAnalyzer;
		private boolean isRunning = false;

		public void startJarHandler() {
			if(!isRunning) {
				isRunning = true;
				jarMonitor = new JarMonitor();
				jarAnalyzer = new JarDirectoryAnalyzer();
				jarAnalyzer.register(jarMonitor);
			}
		}
		
		public void stopJarHandler() throws IOException {
			if(isRunning) {
				jarMonitor.stopUpdate();
				isRunning = false;
			}
		}
		
		private void loadJars() throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, IOException {
			File[] fileList = new File(dirOfJarsPath).listFiles();
			for(File fileIter : fileList) {
				if(isJarFile(fileIter)) {
					factoryCommandLoader.load(fileIter.getPath());
				}
			}			
		}
	}
	/**********************************************
	 * Jar Monitor 
	 **********************************************/
	private class JarMonitor implements DirMonitor{
		private WatchService watcher;
		private Dispatcher<Path> dispatcher = new Dispatcher<>();
		private WatcherThread watcherThread = new WatcherThread();; 		
		private boolean keepWatching = true;
		private Path dirPath = Paths.get(dirOfJarsPath);

		public JarMonitor() {
			startWatching();
		}
		
		public void startWatching() {
			try {
				watcher = FileSystems.getDefault().newWatchService();
				dirPath.register(watcher, ENTRY_MODIFY);
			} catch (IOException e) {
				e.printStackTrace();
			}
			watcherThread.start();	
		}
		
		@Override
		public void stopUpdate() throws IOException {
			keepWatching = false;
			watcher.close();	
		}
		
		@Override
		public void register(Callback<Path> callback) {
			dispatcher.register(callback);
		}

		@Override
		public void unregister(Callback<Path> callback) {
			dispatcher.unregister(callback);
		}
		
		public void updateAll(Path changed) {
			dispatcher.updateAll(changed);
		}
		
		private class WatcherThread extends Thread {
			private WatchKey key;
			
			@Override
			public void run() {	
				
				while (keepWatching) {
				    try {
						key = watcher.take();
					} catch (InterruptedException | ClosedWatchServiceException e) {
						System.out.println(e.getMessage());
					}
				    
				    handleChangedFileEvent(); 
				    
			        if (!key.reset()) {
			        	System.out.println("Key has been unregistered");
			        	return;
			        }
				 }
			}

			private void handleChangedFileEvent() {
				Path changedFile;
				for(WatchEvent<?> event : key.pollEvents()) {
					changedFile = (Path) event.context();

					if (isJarFile(changedFile.toFile())) { 
						updateAll(dirPath.resolve((Path) event.context()));
					}
				}
			}
		}
		
		private boolean isJarFile(File file) {			
			return file.getName().endsWith(JAR_EXTENSTION) && file.isFile();
		}
	}
	
	/**********************************************
	 * Jar Directory Analyzer
	 **********************************************/	
	private class JarDirectoryAnalyzer {
		private Callback<Path> callback = new Callback<>((fileChangedPath) -> {LoadChangedFile(fileChangedPath);} , (param) -> {stop();});
		
		public void register(JarMonitor jarMonitor) {						
			jarMonitor.register(callback);
		}
		
		private void LoadChangedFile(Path fileChanged) {
			try {
				factoryCommandLoader.load(fileChanged.toString());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private void stop() {
			System.out.println("stoped");
		}
	}

	/**********************************************
	 * Jar Loader 
	 **********************************************/
	private static class JarLoader {
		private static final String DOT = ".";
		private static final String SLASH = "/";
		private final static String CLASS_EXTENSTION = ".class";
		private static final String FILE_PREFIX = "file:\\";

		public static List<Class<?>> load(String interfaceName, String jarPath) throws ClassNotFoundException, IOException {
			List<Class<?>> classListToReturn = new ArrayList<>();
			try (JarFile jarFile = new JarFile(jarPath)) {
				URLClassLoader classLoader = new URLClassLoader(new URL[] { new URL(FILE_PREFIX + jarPath)});
				Enumeration<JarEntry> entries = jarFile.entries();
				
				while(entries.hasMoreElements()){
					 JarEntry entry = entries.nextElement();
					 
		             if(isClassFile(entry)){
		                 Class<?> currentClass = Class.forName(getClassName(entry), false, classLoader);
		                 
		                 if(checkIfClassImplementsInterfac(interfaceName, currentClass)) {
		                	 classListToReturn.add(currentClass);
		                 }
		             }
				}
					
				return classListToReturn;
			}
		}

		private static String getClassName(JarEntry entry) {
			String className;
			className = entry.getName();
			className = removeExtensionClass(className).replaceAll(SLASH, DOT);
			
			return className;
		}

		private static boolean isClassFile(JarEntry entry) {
			return !entry.isDirectory() && checkIfExtensionIsClass(entry);
		}
		 
		private static boolean checkIfExtensionIsClass(JarEntry entry) {
	        if(entry.getName().endsWith(CLASS_EXTENSTION)){
	       	 	return true;
	        }
	        
	        return false;
		}
		
		private static String removeExtensionClass(String str) {
			str = str.substring(0, str.lastIndexOf(DOT));
			
			return str;
		}
		
		private static boolean checkIfClassImplementsInterfac(String interfaceName, Class<?> currentClass) {
			 String currentInterface;
			 
	         for(Class<?> element : currentClass.getInterfaces()) {
	        	 currentInterface = element.getName();
	        	 if(interfaceName.equals(currentInterface.substring(currentInterface.lastIndexOf(DOT) + 1))){
	        		 return true;
	        	 }
	         }
	         
	         return false;
		}
	}
	
	/**********************************************
	 * private method
	 **********************************************/
	
	public static void createDBIfNotExist(String databaseName) throws SQLException {
		if(!companiesMap.containsKey(databaseName)) {
			addDatabase(databaseName);
		}
	}
	
	private static void addDatabase(String databaseName) throws SQLException {
		companiesMap.put(databaseName,
								  new DatabaseManagementServer(URL, USER_NAME, USER_PASSWORD, databaseName));
	}
	
	private void sendResponseMessage(ClientInfo clientInfo, String responseMessage) {
		try {
			JSONObject response = new JSONObject();
			response.put("commandType", responseMessage);
			clientInfo.getConnection().sendMessage(clientInfo, getBufferFromJSON(response));
		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}
	}

	private ByteBuffer getBufferFromJSON(JSONObject responseMessage) throws UnsupportedEncodingException {
		byte[] array = responseMessage.toString().getBytes("UTF-8");
		return ByteBuffer.wrap(array);
	}
}

