//package il.co.ilrd.gatewayserver;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.NotActiveException;
//import java.io.OutputStream;
//import java.net.InetSocketAddress;
//import java.net.SocketAddress;
//import java.nio.ByteBuffer;
//import java.nio.channels.Channel;
//import java.nio.channels.DatagramChannel;
//import java.nio.channels.SelectionKey;
//import java.nio.channels.Selector;
//import java.nio.channels.ServerSocketChannel;
//import java.nio.channels.SocketChannel;
//import java.nio.charset.Charset;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//import java.util.Set;
//import java.util.concurrent.SynchronousQueue;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;
//
//import com.sun.net.httpserver.Headers;
//import com.sun.net.httpserver.HttpExchange;
//import com.sun.net.httpserver.HttpHandler;
//import com.sun.net.httpserver.HttpServer;
//
//import il.co.ilrd.http_message.HttpBuilder;
//import il.co.ilrd.http_message.HttpParser;
//import il.co.ilrd.http_message.HttpStatusCode;
//import il.co.ilrd.http_message.HttpVersion;
//
//public class David {
//	private ThreadPoolExecutor threadPool;
//	private CMDFactory<FactoryCommand, CommandKey, Void> cmdFactory = new CMDFactory<>();
//	private JsonToRunnableConvertor jsonToRunnableConvertor = new JsonToRunnableConvertor();
//	private ConnectionsHandler connectionsHandler = new ConnectionsHandler();
//	private MessageHandler messageHandler = new MessageHandler();
//	private final static int DEAFULT_NUM_THREADS = Runtime.getRuntime().availableProcessors();
//	private static final int BUFFER_CAP = 8192;
//	
//	public David(int numOfThreads) {
//		threadPool = new ThreadPoolExecutor(numOfThreads, 
//											Integer.MAX_VALUE, 
//											1, TimeUnit.DAYS, new SynchronousQueue<Runnable>());
//		cmdFactory.add(CommandKey.COMPANY_REGISTRATION, (Void a) -> new CompanyRegistrationCMD());
//		cmdFactory.add(CommandKey.PRODUCT_REGISTRATION, (Void a) -> new ProductRegistrationCMD());
//		cmdFactory.add(CommandKey.IOT_USER_REGISTRATION, (Void a) -> new IOTUserRegistrationCMD());
//		cmdFactory.add(CommandKey.IOT_UPDATE, (Void a) -> new IOTUpdateCMD());
//	}
//		
//	public David() {
//		this(DEAFULT_NUM_THREADS);
//	}
//	
//	public void addHttpServer(ServerPort serverPort) throws IOException {
//		checkIfStarted();
////		connectionsHandler.addHighLevelHttpServer(serverPort.getPort());
//		int port = serverPort.getPort();
//		connectionsHandler.addTcpPort(port);
//		connectionsHandler.addConnection(new LowLevelHttpServer(port));
//	}
//	
//	public void addTcpServer(ServerPort serverPort) throws NotActiveException {
//		checkIfStarted();
//		int port = serverPort.getPort();
//		connectionsHandler.addTcpPort(port);
//		connectionsHandler.addConnection(new TcpServer(port));
//	}
//	
//	public void addUdpServer(ServerPort serverPort) throws NotActiveException {
//		checkIfStarted();
//		int port = serverPort.getPort();
//		connectionsHandler.addUdpPort(port);
//		connectionsHandler.addConnection(new UdpServer(port));
//	}
//	
//	public void start() {
//		connectionsHandler.run();
//	}
//	
//	public void stop() throws IOException {
//		connectionsHandler.stopConnection();
//	}
//	
//	public void setNumOfThreads(int numOfThread) {
//		threadPool.setCorePoolSize(numOfThread);
//	}
//	
//	private class ConnectionsHandler implements Runnable {
//		private List<ServerConnection> serverconnections = new LinkedList<>();
//		private Map<Channel, ServerConnection> socketconnections = new HashMap<>();
//		private Map<SocketChannel, ClientInfo> socketClientInfo = new HashMap<>();
//		private List<Integer> tcpPorts = new ArrayList<>();
//		private List<Integer> udpPorts = new ArrayList<>();
//		private Selector selector;
//		private boolean isRunning = false;
//		private HighLevelHttpServer HighLevelHttpServer = null;
//		
//		public void stopConnection() throws IOException {
//			System.out.println("closing server");
//			if(null != HighLevelHttpServer) {
//				HighLevelHttpServer.stopServer();
//			}
//			isRunning = false;
//			for (SelectionKey keyIter : selector.keys()) {
//				keyIter.channel().close();
//			}
//			selector.close();
//		}
//
//		private void addConnection(ServerConnection connection) {
//			serverconnections.add(connection);
//		}
//
//		private void addUdpPort(int port) throws NotActiveException {
//			if (udpPorts.contains(port)) {
//				throw new NotActiveException("port already exist");
//			}
//			udpPorts.add(port);
//		}
//
//		private void addTcpPort(int port) throws NotActiveException {
//			if (tcpPorts.contains(port)) {
//				throw new NotActiveException("port already exist");
//			}
//			tcpPorts.add(port);
//		}
//		
//
//		@Override
//		public void run() {
//			isRunning = true;
//			try {
//				selector = Selector.open();
//				for (ServerConnection connectionIter : serverconnections) {
//					connectionIter.initServerConnection();
//					socketconnections.put(connectionIter.getChannel(), connectionIter);
//				}
//				runHighLevelHttpServerIfNeeded();
//						
//				while (true) {
//					if (0 == selector.select(20000)) {
//						System.out.println("server running");
//						continue;
//					}
//					Set<SelectionKey> selectedKeys = selector.selectedKeys();
//					Iterator<SelectionKey> iter = selectedKeys.iterator();
//					while (iter.hasNext()) {
//						SelectionKey key = iter.next();
//						if (key.isAcceptable()) {
//							System.out.println("accept");
//							createAndRegisterClientSoket(key);
//						}
//						if (key.isReadable() && key.isValid()) {
//							System.out.println("read");
//							Channel clientChannel = key.channel();
//							socketconnections.get(clientChannel).handleRequestMessage(socketClientInfo.get(clientChannel));
//						}
//					}
//					iter.remove();
//				}
//
//			} catch (Exception e) {
//				if (isRunning) {
//					e.printStackTrace();
//					throw new RuntimeException(e);
//				}
//			}
//		}
//
//		private void runHighLevelHttpServerIfNeeded() {
//			if(null != HighLevelHttpServer) {
//				HighLevelHttpServer.start();
//			}
//		}
//
//		private void createAndRegisterClientSoket(SelectionKey key) throws IOException {
//			ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
//			SocketChannel client = serverChannel.accept();
//			ServerConnection connection = socketconnections.get(serverChannel);
//			socketconnections.put(client, connection);
//			client.configureBlocking(false);
//			client.register(connectionsHandler.getSelector(), SelectionKey.OP_READ);
//			socketClientInfo.put(client, new ClientInfo(connection, client));
//		}
//
//		private Selector getSelector() {
//			return selector;
//		}
//
//		private void closeAndRemoveClient(Channel client) throws IOException {
//			client.close();
//			connectionsHandler.socketconnections.remove(client);
//		}
//				
//		public void addHighLevelHttpServer(int portNumber) throws IOException {
//			HighLevelHttpServer = new HighLevelHttpServer(portNumber);
//			serverconnections.add(HighLevelHttpServer);
//		}
//	}
//
//	/********************************MESSAGE_HANDLER**************************************/
//	
//	private class MessageHandler {
//		private static final String JSON_SYNTAX_ERROR = "{ \"errorMessage\" : \"json syntax error\" }"; 
//		private static final String COMMAND_KEY_ERROR = "{ \"errorMessage\" : \"commandKey not found\" }"; 
//		private static final String JSON_FIELD_NAME_ERROR = "{ \"errorMessage\" : \"json wrong field name\" }"; 
//		
//		private void handleMessage(String bodyJsonString, ClientInfo clientInfo) throws IOException {
//			Runnable runnable;
//			try {
//				runnable = jsonToRunnableConvertor.convertToRunnable(bodyJsonString, clientInfo);
//				threadPool.submit(runnable);			
//			} catch (ParseException e) {
//				clientInfo.getConnection().sendErrorResponseMessage(JSON_SYNTAX_ERROR, clientInfo);
//			} catch (IllegalArgumentException | ClassCastException e) {
//				clientInfo.getConnection().sendErrorResponseMessage(COMMAND_KEY_ERROR, clientInfo);
//			} catch (NullPointerException e) {
//				clientInfo.getConnection().sendErrorResponseMessage(JSON_FIELD_NAME_ERROR, clientInfo);
//			}
//		}
//	}
//	
//	private interface ServerConnection {
//		public void initServerConnection() throws IOException;
//		public void handleRequestMessage(ClientInfo clientInfo) throws IOException;
//		public void sendResponseMessage(String jsonString, ClientInfo clientInfo) throws IOException;
//		public void sendErrorResponseMessage(String jsonString, ClientInfo clientInfo) throws IOException;
//		public Channel getChannel();
//	}
//		
//	/**************************HIGH_LEVEL_SERVER***********************************/	
//	
//	class HighLevelHttpServer implements ServerConnection {
//		private HttpServer httpServer;
//		private final int portNumber;
//		
//		HighLevelHttpServer(int portNumber) throws IOException {
//			this.portNumber = portNumber;
//		}
//
//		void start() {
//			httpServer.start();
//		}
//		
//		void stopServer (){
//			httpServer.stop(0);
//		}
//		
//		private class DefaultHandler implements HttpHandler {
//			@Override
//			public void handle(HttpExchange httpExchange) throws IOException {			
//				try {
//					handleRequestMessage(new ClientInfo(HighLevelHttpServer.this, httpExchange));
//				} catch (IOException e) {
//					e.printStackTrace();
//				}				
//			}
//		}
//
//		@Override
//		public void initServerConnection() throws IOException {
//			httpServer = HttpServer.create(new InetSocketAddress(portNumber), 0);
//			httpServer.createContext("/", new DefaultHandler());
//		}
//
//		@Override
//		public void handleRequestMessage(ClientInfo clientInfo) throws IOException {
//			String bodyJsonString = getBodyJsonString(clientInfo.getHttpExchange());
//			messageHandler.handleMessage(bodyJsonString, clientInfo);
//		}
//
//		@Override
//		public void sendResponseMessage(String jsonString, ClientInfo clientInfo) throws IOException {
//			sendResponse(clientInfo.getHttpExchange(), HttpStatusCode.OK, jsonString);
//			
//		}
//		
//		@Override
//		public void sendErrorResponseMessage(String jsonString, ClientInfo clientInfo) throws IOException {
//			sendResponse(clientInfo.getHttpExchange(), HttpStatusCode.BAD_REQUEST, jsonString);
//		}
//		
//		private String getBodyJsonString(HttpExchange httpExchange) throws IOException {
//			byte[] byteArray = new byte[4096];
//			try(
//				InputStream inputStream = httpExchange.getRequestBody();
//			) {
//				inputStream.read(byteArray);
//				return new String(byteArray).trim();
//			}
//		}
//		
//		private void sendResponse(HttpExchange httpExchange, HttpStatusCode statusCode, String responseBody)
//																								throws IOException {
//			fillHeadersWithStandardHeaders(httpExchange);
//			int responselength = responseBody.length();
//			httpExchange.sendResponseHeaders(statusCode.getCode(), responselength);
//			try (OutputStream outputStream = httpExchange.getResponseBody();) {
//				outputStream.write(responseBody.getBytes());
//			}
//		}
//				
//		@Override
//		public Channel getChannel() {
//			return null;
//		}
//	}
//
//	/**************************LOW_LEVEL_SERVER***********************************/	
//
//	class LowLevelHttpServer implements ServerConnection {
//		private ServerSocketChannel tcpServerChannel;
//		private final int port;
//		private Map<String, String> headersMap = new HashMap<>();
//		private ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_CAP);
//		
//		public LowLevelHttpServer(int port) {
//			this.port = port;
//		}
//
//		@Override
//		public void initServerConnection() throws IOException {
//			tcpServerChannel = ServerSocketChannel.open();
//			tcpServerChannel.configureBlocking(false);
//			tcpServerChannel.bind(new InetSocketAddress(port));
//			tcpServerChannel.register(connectionsHandler.getSelector(), SelectionKey.OP_ACCEPT);			
//		}
//
//		@Override
//		public void handleRequestMessage(ClientInfo clientInfo) throws IOException {
//			String httpString = getMessageString(byteBuffer, clientInfo);
//			if (null != httpString) {
//				HttpParser httpParser = new HttpParser(httpString);
//				messageHandler.handleMessage(httpParser.getBody().getBodyString(), clientInfo);				
//			}
//		}
//
//		@Override
//		public void sendResponseMessage(String jsonString, ClientInfo clientInfo) throws IOException {
//			sendResponse(HttpStatusCode.OK, jsonString, clientInfo);
//		}
//
//		@Override
//		public void sendErrorResponseMessage(String jsonString, ClientInfo clientInfo) throws IOException {
//			sendResponse(HttpStatusCode.BAD_REQUEST, jsonString, clientInfo);
//		}
//
//		@Override
//		public Channel getChannel() {
//			return tcpServerChannel;
//		}
//		
//		private void fillHeafersMapWithStandardHeaders(int responseLength) {
//			Integer resBodySize = responseLength;
//			headersMap.put("Content-Length", resBodySize.toString());
//			headersMap.put("Content-Type", "application/json");
//		}
//		
//		private void sendResponse(HttpStatusCode statusCode, String responseBody, ClientInfo clientInfo) throws IOException {
//			ByteBuffer responseByteBuffer = getResponseByteBuffer(statusCode, responseBody);
//			while (responseByteBuffer.hasRemaining()) {
//				clientInfo.getTcpClientChannel().write(responseByteBuffer);
//			}
//			connectionsHandler.closeAndRemoveClient(clientInfo.getTcpClientChannel());
//		}
//
//		private ByteBuffer getResponseByteBuffer(HttpStatusCode statusCode, String responseBody) {
//			fillHeafersMapWithStandardHeaders(responseBody.length());
//			String responseMessage = HttpBuilder.createHttpResponseMessage(HttpVersion.HTTP_1_1, statusCode, headersMap, responseBody);
//			return ByteBuffer.wrap(responseMessage.getBytes());
//		}
//
//	}
//	
//	/**************************TCP_SERVER***********************************/	
//	
//	private class TcpServer implements ServerConnection {
//		private ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_CAP);
//		private ServerSocketChannel tcpServerChannel;
//		private final int port; 
//		
//		public TcpServer(int port) {
//			this.port = port;
//		}
//
//		@Override
//		public void initServerConnection() throws IOException {
//			tcpServerChannel = ServerSocketChannel.open();
//			tcpServerChannel.configureBlocking(false);
//			tcpServerChannel.bind(new InetSocketAddress(port));
//			tcpServerChannel.register(connectionsHandler.getSelector(), SelectionKey.OP_ACCEPT);
//		}
//
//		@Override
//		public void handleRequestMessage(ClientInfo clientInfo) throws IOException {
//			String bodyString = getMessageString(byteBuffer, clientInfo);
//			if(null != bodyString) {
//				messageHandler.handleMessage(bodyString, clientInfo);				
//			}
//		}
//
//		@Override
//		public void sendResponseMessage(String jsonString, ClientInfo clientInfo) throws IOException {
//			ByteBuffer byteBuffer = Charset.forName("ASCII").encode(jsonString);
//			while (byteBuffer.hasRemaining()) {
//				clientInfo.getTcpClientChannel().write(byteBuffer);
//			}
//		}
//		
//		@Override
//		public void sendErrorResponseMessage(String jsonString, ClientInfo clientInfo) throws IOException {
//			sendResponseMessage(jsonString, clientInfo);
//		}
//
//		@Override
//		public Channel getChannel() {
//			return tcpServerChannel;
//		}
//	}
//
//	
///**************************UDP_SERVER***********************************/	
//	
//	private class UdpServer implements ServerConnection {
//		private ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_CAP);
//		private DatagramChannel udpServerChannel;
//		private final int port;
//
//		public UdpServer(int port) {
//			this.port = port;
//		}
//		
//		@Override
//		public void initServerConnection() throws IOException {
//			udpServerChannel = DatagramChannel.open();
//			udpServerChannel.configureBlocking(false);
//			udpServerChannel.socket().bind(new InetSocketAddress(port));
//			udpServerChannel.register(connectionsHandler.getSelector(), SelectionKey.OP_READ);			
//		}
//
//		@Override
//		public void handleRequestMessage(ClientInfo clientInfo) throws IOException {
//			byteBuffer.clear();
//			SocketAddress socketAddress = udpServerChannel.receive(byteBuffer);
//			if (null == socketAddress) {
//				System.out.println("null");
//			}
//			byteBuffer.flip();
//			String bodyString = new String(Charset.forName("ASCII").decode(byteBuffer).array());
//			messageHandler.handleMessage(bodyString, new ClientInfo(this, socketAddress));
//		}
//
//		@Override
//		public void sendResponseMessage(String jsonString, ClientInfo clientInfo) throws IOException {
//			ByteBuffer byteBuffer = Charset.forName("ASCII").encode(jsonString);			
//			udpServerChannel.send(byteBuffer, clientInfo.getUdpClientAddress());
//		}
//
//		@Override
//		public void sendErrorResponseMessage(String jsonString, ClientInfo clientInfo) throws IOException {
//			sendResponseMessage(jsonString, clientInfo);
//		}
//
//		@Override
//		public Channel getChannel() {
//			return udpServerChannel;
//		}
//	}
//	
//	
///************************************PRIVATE_CLASESS***************************************/	
//
//	private  class JsonToRunnableConvertor {
//		private JSONParser parser = new JSONParser();
//		
//		private Runnable convertToRunnable(String jsonString, ClientInfo clientInfo) throws ParseException {
//			JSONObject jsonObject = (JSONObject) parser.parse(jsonString);
//			
//			return new IOTRunnable((String)jsonObject.get("CommandKey"),
//											jsonObject.get("Data"), 
//											clientInfo);
//		}
//	}
//	
//	private class IOTRunnable implements Runnable {
//		private CommandKey commandKey;
//		private Object data;
//		ClientInfo clientInfo;
//
//		public IOTRunnable(String commandKey, Object data, ClientInfo clientInfo) {
//			Objects.requireNonNull(commandKey);
//			Objects.requireNonNull(data);
//			this.commandKey = CommandKey.valueOf(commandKey);
//			this.data = data;
//			this.clientInfo = clientInfo;
//		}
//
//		@Override
//		public void run() {
//			FactoryCommand factoryCommand = cmdFactory.create(commandKey);
//			try {
//				factoryCommand.run(data, clientInfo);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//	
//	private class CompanyRegistrationCMD implements FactoryCommand {
//		private static final String RESPONSE_MESSAGE = "{ \"message\": \"company registrated\" }";
//		
//		@Override
//		public void run(Object data, ClientInfo clientInfo) throws IOException {
//			clientInfo.getConnection().sendResponseMessage(RESPONSE_MESSAGE, clientInfo);
//		}
//	}
//	
//	private class ProductRegistrationCMD implements FactoryCommand {
//		private static final String RESPONSE_MESSAGE = "{ \"message\": \"product registrated\" }";
//		
//		@Override
//		public void run(Object data, ClientInfo clientInfo) throws IOException {
//			clientInfo.getConnection().sendResponseMessage(RESPONSE_MESSAGE, clientInfo);
//		}
//	}
//	
//	private class IOTUserRegistrationCMD implements FactoryCommand {
//		private static final String RESPONSE_MESSAGE = "{ \"message\": \"IOT user registrated\" }";
//		
//		@Override
//		public void run(Object data, ClientInfo clientInfo) throws IOException {
//			clientInfo.getConnection().sendResponseMessage(RESPONSE_MESSAGE, clientInfo);
//		}
//	}
//	
//	private class IOTUpdateCMD implements FactoryCommand {
//		private static final String RESPONSE_MESSAGE = "{ \"message\": \"IOT updated\" }";
//		
//		@Override
//		public void run(Object data, ClientInfo clientInfo) throws IOException {
//			clientInfo.getConnection().sendResponseMessage(RESPONSE_MESSAGE, clientInfo);
//		}
//	}
//		
//	private class ClientInfo {
//		private SocketChannel tcpClientChannel;
//		private SocketAddress udpClientAddress;
//		private HttpExchange httpExchange;
//		private ServerConnection connection;
//
//		public ClientInfo(ServerConnection connection, SocketChannel tcpClientChannel) {
//			this.tcpClientChannel = tcpClientChannel;
//			this.connection = connection;
//		}
//
//		public ClientInfo(ServerConnection connection, SocketAddress udpClientAddress) {
//			this.udpClientAddress = udpClientAddress;
//			this.connection = connection;
//		}
//		
//		public ClientInfo(ServerConnection connection, HttpExchange httpExchange) {
//			this.httpExchange = httpExchange;
//			this.connection = connection;
//		}
//
//		private SocketChannel getTcpClientChannel() {
//			return tcpClientChannel;
//		}
//
//		private SocketAddress getUdpClientAddress() {
//			return udpClientAddress;
//		}
//
//		private ServerConnection getConnection() {
//			return connection;
//		}
//		
//		private HttpExchange getHttpExchange() {
//			return httpExchange;
//		}
//	}
//
//	private interface FactoryCommand {
//		public void run(Object data, ClientInfo clientInfo) throws IOException;
//	}
//	
///**************************PRIVATE_UTILS_METHODS***********************************/
//		
//	private void checkIfStarted() throws NotActiveException {
//		if (connectionsHandler.isRunning) {
//			throw new NotActiveException("cannot add connection after the server is running");
//		}
//	}
//	
//	private String getMessageString(ByteBuffer byteBuffer, ClientInfo clientInfo) throws IOException {
//		String messageString = null;
//		byteBuffer.clear();
//		SocketChannel socketChannel = clientInfo.getTcpClientChannel();
//		if (-1 == socketChannel.read(byteBuffer)) {
//			connectionsHandler.closeAndRemoveClient(socketChannel);
//			System.out.println("client is closed");
//		} else {
//			byteBuffer.flip();
//			messageString = new String(Charset.forName("ASCII").decode(byteBuffer).array());
//		}
//		return messageString;
//	}
//	
//	private void fillHeadersWithStandardHeaders(HttpExchange httpExchange) {
//		Headers headers = httpExchange.getResponseHeaders();
//		headers.add("Content-Type", "application/json");
//	}		
//}