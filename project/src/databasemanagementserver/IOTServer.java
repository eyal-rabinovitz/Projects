package databasemanagementserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class IOTServer {
	private ConnectionHandler connectionHandler = new ConnectionHandler();
	private MessageHandler messageHandler = new MessageHandler();
	private boolean isServerRunning = false;
	
	public void startServer() {
		isServerRunning = true;
		connectionHandler.startConnections();
	}

	public void stopServer() throws IOException {
		isServerRunning = false;
		connectionHandler.stopConnections();
	}

	public void addBroadcastConnection(int portNumber) throws Exception {
		if(isAddConnectionPossible(portNumber, connectionHandler.broadcastPortsInUse)) {
			connectionHandler.addConnection(new BroadcastConnection(portNumber), connectionHandler.broadcastPortsInUse, portNumber);			
		}
	}

	public void addTcpConnection(int portNumber) throws Exception {
		if(isAddConnectionPossible(portNumber, connectionHandler.tcpPortsInUse)) {
			connectionHandler.addConnection(new TcpConnection(portNumber), connectionHandler.tcpPortsInUse, portNumber);
		}
	}

	public void addUdpConnection(int portNumber) throws Exception {
		if(isAddConnectionPossible(portNumber, connectionHandler.udpPortsInUse)) {
			connectionHandler.addConnection(new UdpConnection(portNumber), connectionHandler.udpPortsInUse, portNumber );
		}
	}	
	
	private boolean isAddConnectionPossible (int portNumber, List<Integer> portsInUse) throws Exception {
		if(isServerRunning) {
			throw new Exception("Not possible to add connection becuase server is running");
		}
		if(!isPortAvailable(portNumber, portsInUse)) {
			throw new Exception("Not possible to add connection becuase port number not available");
		}
		
		return true;
	}
	
	private boolean isPortAvailable(int portNumber, List<Integer> portsInUse) {
		return !portsInUse.contains(portNumber);
	}
	
	/**********************************************
	 * Connection Interface
	 **********************************************/
	private interface Connection {
		public void sendMessage(ClientInfo clientInfo, ByteBuffer message) throws IOException;
		public void receiveMessage(Channel channel, ByteBuffer messageBuffer) throws ClassNotFoundException, IOException;
		public void registerChannel(Selector selector) throws UnknownHostException, IOException;
		public Channel getChannel();
		public int getPortNumber();
	}

	/**********************************************
	 * TCP Connection
	 **********************************************/
	private class TcpConnection implements Connection {
		private int portNumber;
		private ServerSocketChannel TcpServerChannel;

		public TcpConnection(int portNumber) throws IOException {
			this.portNumber = portNumber;
			TcpServerChannel = ServerSocketChannel.open();
		}

		@Override
		public void registerChannel(Selector selector) throws UnknownHostException, IOException {
			TcpServerChannel.bind(new InetSocketAddress(portNumber));
			TcpServerChannel.configureBlocking(false);
			TcpServerChannel.register(selector, SelectionKey.OP_ACCEPT);
		}
		
		@Override
		public Channel getChannel() {
			return TcpServerChannel;
		}

		@Override
		public void sendMessage(ClientInfo clientInfo, ByteBuffer message) throws IOException {
			try {
				while(message.hasRemaining()) {
					(clientInfo.tcpSocketChannel).write(message);	
				}
			} finally {
				message.clear();				
			}
		}

		@Override
		public void receiveMessage(Channel channel, ByteBuffer messageBuffer) throws ClassNotFoundException, IOException {
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
	}

	/**********************************************
	 * UDP Connection
	 **********************************************/
	private class UdpConnection implements Connection {
		private int portNumber;
		private DatagramChannel udpServer;
		
		private UdpConnection(int portNumber) throws IOException {
			this.portNumber = portNumber;
			udpServer = DatagramChannel.open();
		}

		@Override
		public void registerChannel(Selector selector) throws IOException {
			udpServer.socket().bind(new InetSocketAddress(portNumber));
			udpServer.configureBlocking(false);
			udpServer.register(selector, SelectionKey.OP_READ);	
		}

		@Override
		public Channel getChannel() {
			return udpServer;
		}

		@Override
		public void sendMessage(ClientInfo clientInfo, ByteBuffer message) throws IOException {
			udpServer.send(message, clientInfo.udpSocketAddress);
		}
		
		@Override
		public void receiveMessage(Channel channel, ByteBuffer messageBuffer) throws IOException, ClassNotFoundException {
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
	}
	
	/**********************************************
	 * Broadcast Connection
	 **********************************************/
	private class BroadcastConnection extends UdpConnection {
		private BroadcastConnection(int portNumber) throws IOException {
			super(portNumber);
		}
	}
	
	/**********************************************
	 * Connection Handler
	 **********************************************/
	private class ConnectionHandler implements Runnable {
		private Selector selector;
		private static final int BUF_SIZE = 2048;
		private static final int TIMEOUT = 5000;
		private List<Connection> connectionList = new ArrayList<>();
		private List<Integer> tcpPortsInUse = new ArrayList<>();
		private List<Integer> udpPortsInUse = new ArrayList<>();
		private List<Integer> broadcastPortsInUse = new ArrayList<>();
		private Map<Channel, Connection> mapChannelConnection = new HashMap<>();
		
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
				for(Connection connection : connectionList) {
					connection.registerChannel(selector);
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
							Connection currentConnection = mapChannelConnection.get(currentChannel);
							currentConnection.receiveMessage(currentChannel, messageBuffer);
						}
						iter.remove();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
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
			for(SelectionKey key : selector.keys()) {
				key.channel().close();
			}
		}

		private void addConnection(Connection connection, List<Integer> portsInUse, int portNumber) {
			connectionList.add(connection);
			portsInUse.add(portNumber);
		}
	}
	
	/**********************************************
	 * Protocol
	 **********************************************/
	private interface Protocol {
		public void handleMessage(ClientInfo clientInfo, Message<?, ?> message) throws IOException;
	}

	/**********************************************
	 * Ping Pong Protocol
	 **********************************************/
	private class PingPongProtocol implements Protocol {
		private static final String PING_MESSAGE = "Ping\n";
		private static final String PONG_MESSAGE = "Pong\n";
		private final Message<ProtocolType, Message<?, ?>> pingMessage = new ServerMessage(ProtocolType.PINGPONG, new PingPongServerMessage(PING_MESSAGE));
		private final Message<ProtocolType, Message<?, ?>> pongMessage = new ServerMessage(ProtocolType.PINGPONG, new PingPongServerMessage(PONG_MESSAGE));
		private final Message<ProtocolType, Message<?, ?>> wrongMessage = new ServerMessage(ProtocolType.PINGPONG, new PingPongServerMessage("Wrong message\n"));

		@Override
		public void handleMessage(ClientInfo clientInfo, Message<?, ?> message) {
			Message<ProtocolType, Message<?, ?>> receiveMessage = getOutputAccordingToInput((String)message.getKey());

			try {
				byte[] response = ByteUtil.toByteArray(receiveMessage);
				ByteBuffer buffer = ByteBuffer.wrap(response);
				clientInfo.connection.sendMessage(clientInfo, buffer);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private Message<ProtocolType, Message<?, ?>> getOutputAccordingToInput(String input) {
			if(input.equals(PING_MESSAGE)) {
				return pongMessage;
			} else if(input.equals(PONG_MESSAGE)) {
				return pingMessage;
			} 

			return wrongMessage;
		}
	}
	
	/**********************************************
	 * Chat Server Protocol
	 **********************************************/
	private class ChatServerProtocol implements Protocol {
		private Map<ChatProtocolKeys, BiFunction<ChatServerMessage, ClientInfo, ServerMessage>> chatMethodsMap = new HashMap<>();
		private List<ChatServerClient> clientList = new ArrayList<>();
		private static final String SENT_MESSAGE = "Message sent successfully ";
		private static final String WELLCOME_MESSAGE = " just joined the chat ";
		private static final String ALREADY_REGISTERED_MESSAGE = "Error, already registered! ";
		private static final String NOT_REGISTERED_MESSAGE = "Error, you are not registered! ";
		private static final String INVALID_KEY_MESSAGE = "invalid key ";
		private static final String LEAVE_MESSAGE = " just left the chat ";
		private static final int BUFFER_SIZE = 2048;
		
		public ChatServerProtocol() {
			chatMethodsMap.put(ChatProtocolKeys.MESSAGE, new BroadcasMessageHandler());
			chatMethodsMap.put(ChatProtocolKeys.REMOVE_REQUEST, new RemoveHandler());
			chatMethodsMap.put(ChatProtocolKeys.REGISTRATION_REQUEST, new RegistrationHandler());
			chatMethodsMap.put(ChatProtocolKeys.ERROR_MESSAGE, new WrongKeyHandler());
			chatMethodsMap.put(ChatProtocolKeys.REGISTRATION_ACK, new WrongKeyHandler());
			chatMethodsMap.put(ChatProtocolKeys.BROADCAST_MESSAGE, new WrongKeyHandler());
			chatMethodsMap.put(ChatProtocolKeys.REGISTRATION_REFUSE, new WrongKeyHandler());
			chatMethodsMap.put(ChatProtocolKeys.NEW_CLIENT_REGISTRATION, new WrongKeyHandler());
		}
		
		@Override
		public void handleMessage(ClientInfo clientInfo, Message<?, ?> message) throws IOException {
			ByteBuffer messageBuffer = ByteBuffer.allocate(BUFFER_SIZE); 
			ChatServerMessage chatMessage = (ChatServerMessage)message;			
			ServerMessage outputMessage = chatMethodsMap.get(chatMessage.getKey()).apply(chatMessage, clientInfo);
			
			messageBuffer.put(ByteUtil.toByteArray(outputMessage));
			messageBuffer.flip();
			clientInfo.connection.sendMessage(clientInfo, messageBuffer);
		}
		
		private class BroadcasMessageHandler implements BiFunction<ChatServerMessage, ClientInfo, ServerMessage> {
			@Override
			public ServerMessage apply(ChatServerMessage message, ClientInfo clientInfo) {
				if(!isRegistered(clientInfo)) {
					return buildServerMessage(NOT_REGISTERED_MESSAGE, ChatProtocolKeys.MESSAGE);
				}

				sendToAllExceptOne(findClient(clientInfo).getName() + ": " + message.getData(), ChatProtocolKeys.BROADCAST_MESSAGE, clientInfo);
				
				return buildServerMessage(SENT_MESSAGE, ChatProtocolKeys.BROADCAST_MESSAGE);
			}
		}
		
		private class RegistrationHandler implements BiFunction<ChatServerMessage, ClientInfo, ServerMessage> {
			@Override
			public ServerMessage apply(ChatServerMessage message, ClientInfo clientInfo) {
				if(isRegistered(clientInfo)) {
					return buildServerMessage(ALREADY_REGISTERED_MESSAGE, ChatProtocolKeys.REGISTRATION_REFUSE);
				}
				
				ChatServerClient newClient = new ChatServerClient(clientInfo, message.getData());
				clientList.add(newClient);

				sendToAllExceptOne(newClient.getName() + WELLCOME_MESSAGE, ChatProtocolKeys.NEW_CLIENT_REGISTRATION, clientInfo);
				
				return buildServerMessage(newClient.getName() + WELLCOME_MESSAGE, ChatProtocolKeys.REGISTRATION_ACK);
			}
		}
		
		private class RemoveHandler implements BiFunction<ChatServerMessage, ClientInfo, ServerMessage> {
			@Override
			public ServerMessage apply(ChatServerMessage message, ClientInfo clientInfo) {
				if(!isRegistered(clientInfo)) {
					return buildServerMessage(NOT_REGISTERED_MESSAGE, ChatProtocolKeys.MESSAGE);
				}
				ChatServerClient clientToRemove = findClient(clientInfo);
				String clientRemovedName = clientToRemove.getName();
				clientList.remove(clientToRemove);

				sendToAllExceptOne(clientRemovedName + LEAVE_MESSAGE, ChatProtocolKeys.BROADCAST_MESSAGE, clientInfo);
				
				return buildServerMessage(clientRemovedName + LEAVE_MESSAGE, ChatProtocolKeys.BROADCAST_MESSAGE);
			}
		}
		
		private class WrongKeyHandler implements BiFunction<ChatServerMessage, ClientInfo, ServerMessage> {
			@Override
			public ServerMessage apply(ChatServerMessage message, ClientInfo clientInfo) {
				return buildServerMessage(INVALID_KEY_MESSAGE, ChatProtocolKeys.ERROR_MESSAGE);
			}
		}
		
		private boolean isRegistered(ClientInfo clientInfo) {
			if(null == findClient(clientInfo)) {
				return false;				
			}
			
			return true;
		}
		
		private ChatServerClient findClient(ClientInfo clientInfo) {
			for(ChatServerClient chatServerClient : clientList) {
				if(chatServerClient.getClientInfo().tcpSocketChannel.equals(clientInfo.getTcpSocketChannel())) {
					return chatServerClient;
				}
			}
			
			return null;
		}
		
		private void sendToAllExceptOne(String messageString, ChatProtocolKeys key, ClientInfo clientInfo) {
			ServerMessage message = buildServerMessage(messageString, key);
			ByteBuffer messageBuffer = ByteBuffer.allocate(BUFFER_SIZE); 
			try {
				for(ChatServerClient chatServerClient : clientList) {
					messageBuffer.clear();
					messageBuffer.put(ByteUtil.toByteArray(message));
					messageBuffer.flip();
					if(!chatServerClient.getName().equals(findClient(clientInfo).getName())) {
						chatServerClient.getClientInfo().connection.sendMessage(chatServerClient.getClientInfo(), messageBuffer);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private ServerMessage buildServerMessage(String messageString, ChatProtocolKeys key) {
			return new ServerMessage(ProtocolType.CHAT_SERVER, new ChatServerMessage(key, messageString));
		}

	}
	
	/**********************************************
	 * DBFunction interface
	 **********************************************/
	public interface DBFunction<T, U, Z> {
		public void apply(T t, U u, Z z) throws SQLException;
	}

	/**********************************************
	 * Database Management Protocol
	 **********************************************/
	private class DatabaseManagementProtocol implements Protocol {
		private Map<String, DatabaseManagementServer> companiesMap = new HashMap<>();
		private Map<DatabaseKeys, DBFunction<ClientInfo, String, List<Object>>> databaseMethodsMap = new HashMap<>();
		private static final int BUFFER_SIZE = 2048;
		private static final String URL = "jdbc:mysql://localhost";
		private static final String USER_NAME = "root";
		private static final String USER_PASSWORD = "132435"; 
		private DatabaseManagementMessage databaseMessage;
		
		public DatabaseManagementProtocol() {
			databaseMethodsMap.put(DatabaseKeys.CREATE_COMPANY_DATABASE, new CreateCompanyDatabase());
			databaseMethodsMap.put(DatabaseKeys.CREATE_TABLE, new CreateTable());
			databaseMethodsMap.put(DatabaseKeys.DELETE_TABLE, new DeleteTable());
			databaseMethodsMap.put(DatabaseKeys.CREATE_IOT_EVENT, new CreateIOTEvent());
			databaseMethodsMap.put(DatabaseKeys.CREATE_ROW, new CreateRow());
			databaseMethodsMap.put(DatabaseKeys.READ_ROW, new ReadRow());
			databaseMethodsMap.put(DatabaseKeys.READ_FIELD_BY_NAME, new ReadFieldByName());
			databaseMethodsMap.put(DatabaseKeys.READ_FIELD_BY_INDEX, new ReadFieldByIndex());
			databaseMethodsMap.put(DatabaseKeys.UPDATE_FIELD_BY_NAME, new UpdateFieldByName());
			databaseMethodsMap.put(DatabaseKeys.UPDATE_FIELD_BY_INDEX, new UpdateFieldByIndex());
			databaseMethodsMap.put(DatabaseKeys.DELETE_ROW, new DeleteRow());
			databaseMethodsMap.put(DatabaseKeys.ERROR_MESSAGE, new WrongKeyHandler());
			databaseMethodsMap.put(DatabaseKeys.ACK_MESSAGE, new WrongKeyHandler());
		}
		
		@Override
		public void handleMessage(ClientInfo clientInfo, Message<?, ?> message) {
			databaseMessage = (DatabaseManagementMessage)message;
			String databaseName = databaseMessage.getKey().getDatabaseName();
			List<Object> parameters = databaseMessage.getData();

			try {
				if(!isConnectionValid(clientInfo, databaseName, parameters)) {	
					databaseMethodsMap.get(DatabaseKeys.ERROR_MESSAGE).apply(clientInfo, databaseName, parameters);
				}else {
					openMessage(clientInfo, databaseName, parameters);					
				}
			} catch (SQLException | ArrayIndexOutOfBoundsException | ClassCastException e) {
				sendMessage(databaseName, DatabaseKeys.ERROR_MESSAGE, e.getMessage(), clientInfo);
			}
		}

		private boolean isConnectionValid(ClientInfo clientInfo, String databaseName, List<Object> parameters) throws SQLException {
			return((clientInfo.connection.getPortNumber() == ProtocolPort.DATABASE_MANAGEMENT_PORT.getPort()) &&
				(clientInfo.getTcpSocketChannel() instanceof SocketChannel));
		}
		
		private void openMessage(ClientInfo clientInfo, String databaseName, List<Object> parameters) throws SQLException {
			DatabaseKeys databaseKeys= databaseMessage.getKey().getActionType();
			DatabaseManagementServer currentDatabase = companiesMap.get(databaseName);
		
			if(null == currentDatabase) {
				databaseMethodsMap.get(DatabaseKeys.CREATE_COMPANY_DATABASE).apply(clientInfo, databaseName, parameters);
			}
			databaseMethodsMap.get(databaseKeys).apply(clientInfo, databaseName, parameters);
		}
		
		private class CreateCompanyDatabase implements DBFunction<ClientInfo, String, List<Object>> {
			private static final String ACK_CREATED = "Database created";
			private static final String ACK_ALREADY_EXISTS = "Database already exists";

			@Override
			public void apply(ClientInfo clientInfo, String databaseName, List<Object> parameters) throws SQLException {
				if(!companiesMap.containsKey(databaseName)) {
					addDatabase(databaseName, parameters);
					sendMessage(databaseName, DatabaseKeys.ACK_MESSAGE, ACK_CREATED, clientInfo);
				} else {
					sendMessage(databaseName, DatabaseKeys.ERROR_MESSAGE, ACK_ALREADY_EXISTS, clientInfo);
				}
			}
		}

		private class CreateTable implements DBFunction<ClientInfo, String, List<Object>> {
			private static final String ACK = "Table created";

			@Override
			public void apply(ClientInfo clientInfo, String databaseName, List<Object> parameters) throws SQLException {
				companiesMap.get(databaseName).createTable((String)parameters.get(0));
				
				sendMessage(databaseName, DatabaseKeys.ACK_MESSAGE, ACK, clientInfo);
			}
		}
		
		private class DeleteTable implements DBFunction<ClientInfo, String, List<Object>> {
			private static final String ACK = "Table deleted";

			@Override
			public void apply(ClientInfo clientInfo, String databaseName, List<Object> parameters) throws SQLException {
				companiesMap.get(databaseName).deleteTable((String)parameters.get(0));

				sendMessage(databaseName, DatabaseKeys.ACK_MESSAGE, ACK, clientInfo);
			}
		}
		
		private class CreateIOTEvent implements DBFunction<ClientInfo, String, List<Object>> {
			private static final String ACK = "IOTEvent created";
		
			@Override
			public void apply(ClientInfo clientInfo, String databaseName, List<Object> parameters) throws SQLException {
				companiesMap.get(databaseName).createIOTEvent((String)parameters.get(0));

				sendMessage(databaseName, DatabaseKeys.ACK_MESSAGE, ACK, clientInfo);
			}
		}
		
		private class CreateRow implements DBFunction<ClientInfo, String, List<Object>> {
			private static final String ACK = "Row created";

			@Override
			public void apply(ClientInfo clientInfo, String databaseName, List<Object> parameters) throws SQLException {
				companiesMap.get(databaseName).createRow((String)parameters.get(0));

				sendMessage(databaseName, DatabaseKeys.ACK_MESSAGE, ACK, clientInfo); 
			}
		}
		
		private class ReadRow implements DBFunction<ClientInfo, String, List<Object>> {			
			@Override
			public void apply(ClientInfo clientInfo, String databaseName, List<Object> parameters) throws SQLException {
				List<Object> returnValueList = companiesMap.get(databaseName).readRow((String)parameters.get(0),
																					  (String)parameters.get(1),
																					  (Object)parameters.get(2));
				sendMessage(databaseName, DatabaseKeys.ACK_MESSAGE, returnValueList, clientInfo);
			}
		}

		private class ReadFieldByName implements DBFunction<ClientInfo, String, List<Object>> {			
			@Override
			public void apply(ClientInfo clientInfo, String databaseName, List<Object> parameters) throws SQLException {
				Object returnValue = companiesMap.get(databaseName).readField((String)parameters.get(0),
																		(String)parameters.get(1),
																		(Object)parameters.get(2),
																		(String)parameters.get(3));

				sendMessage(databaseName, DatabaseKeys.ACK_MESSAGE, returnValue, clientInfo);
			}
		}
		
		private class ReadFieldByIndex implements DBFunction<ClientInfo, String, List<Object>> {			
			@Override
			public void apply(ClientInfo clientInfo, String databaseName, List<Object> parameters) throws SQLException {
				Object returnValue = companiesMap.get(databaseName).readField((String)parameters.get(0),
																		(String)parameters.get(1),
																		(Object)parameters.get(2),
																		(int)parameters.get(3));

				sendMessage(databaseName, DatabaseKeys.ACK_MESSAGE ,returnValue, clientInfo); 
			}
		}

		private class UpdateFieldByName implements DBFunction<ClientInfo, String, List<Object>> {
			private static final String ACK = "Field is updated";			
			
			@Override
			public void apply(ClientInfo clientInfo, String databaseName, List<Object> parameters) throws SQLException {
				companiesMap.get(databaseName).updateField((String)parameters.get(0),
															(String)parameters.get(1),
															(Object)parameters.get(2),
															(String)parameters.get(3),
															(Object)parameters.get(4));

				sendMessage(databaseName, DatabaseKeys.ACK_MESSAGE, ACK, clientInfo);
			}
		}
		
		private class UpdateFieldByIndex implements DBFunction<ClientInfo, String, List<Object>> {
			private static final String ACK = "Field is updated";			
			
			@Override
			public void apply(ClientInfo clientInfo, String databaseName, List<Object> parameters) throws SQLException {
				companiesMap.get(databaseName).updateField((String)parameters.get(0),
															(String)parameters.get(1),
															(Object)parameters.get(2),
															(int)parameters.get(3),
															(Object)parameters.get(4));

				sendMessage(databaseName, DatabaseKeys.ACK_MESSAGE, ACK, clientInfo);
			}
		}
		
		private class DeleteRow implements DBFunction<ClientInfo, String, List<Object>> {
			private static final String ACK = "Row deleted";			
			
			@Override
			public void apply(ClientInfo clientInfo, String databaseName, List<Object> parameters) throws SQLException {
				companiesMap.get(databaseName).deleteRow((String)parameters.get(0),
														(String)parameters.get(1),
														(Object)parameters.get(2));
				
				sendMessage(databaseName, DatabaseKeys.ACK_MESSAGE, ACK, clientInfo);
			}
		}
		
		private class WrongKeyHandler implements DBFunction<ClientInfo, String, List<Object>> {
			private static final String ERROR_MSG = "Invalid key";
			
			@Override
			public void apply(ClientInfo clientInfo, String databaseName, List<Object> parameters) {
				sendMessage(databaseName, DatabaseKeys.ERROR_MESSAGE, ERROR_MSG, clientInfo); 
			}
		}

		private void addDatabase(String databaseName, List<Object> parameters) throws SQLException {
			companiesMap.put(databaseName,
									  new DatabaseManagementServer(URL, USER_NAME, USER_PASSWORD, (String)parameters.get(0)));
		}
		
		private void sendMessage(String databaseName, DatabaseKeys key, Object returnValue, ClientInfo clientInfo) {
			List<Object> returnList = new ArrayList<>();
			returnList.add(returnValue);
			
			sendMessage(databaseName, key, returnList, clientInfo);
		}
		
		private void sendMessage(String databaseName, DatabaseKeys key, List<Object> returnValue, ClientInfo clientInfo) {
			ServerMessage outputMessage = buildServerMessage(databaseName, key, returnValue);
			try {
				ByteBuffer messageBuffer = createBuffer(outputMessage, clientInfo);
				clientInfo.connection.sendMessage(clientInfo, messageBuffer);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private ByteBuffer createBuffer(ServerMessage outputMessage, ClientInfo clientInfo) throws IOException {
			ByteBuffer messageBuffer = ByteBuffer.allocate(BUFFER_SIZE); 
			messageBuffer.put(ByteUtil.toByteArray(outputMessage));
			messageBuffer.flip();
			
			return messageBuffer;
		}
		
		private ServerMessage buildServerMessage(String databaseName, DatabaseKeys key, List<Object> returnValue) {
			ActionTypeKey actionTypeKey = new ActionTypeKey(databaseName, key);
	
			return new ServerMessage(ProtocolType.DATABASE_MANAGEMENT, new DatabaseManagementMessage(actionTypeKey, returnValue));
		}
	}
	
	/***********************************************
	 * Chat Server Client
	 **********************************************/
	private class ChatServerClient {
		private ClientInfo clientInfo;
		private String name;
		
		public ChatServerClient(ClientInfo clientInfo, String name) {
			this.clientInfo = clientInfo;
			this.name = name;
		}

		public ClientInfo getClientInfo() {
			return clientInfo;
		}

		public String getName() {
			return name;
		}
	}
	
	/***********************************************
	 * Message Handler
	 **********************************************/
	private class MessageHandler {
		private Map<ProtocolType, Protocol> protocolMap = new HashMap<>();
		
		public MessageHandler() {
			addProtocol(new PingPongProtocol(), ProtocolType.PINGPONG);
			addProtocol(new ChatServerProtocol(), ProtocolType.CHAT_SERVER);
			addProtocol(new DatabaseManagementProtocol(), ProtocolType.DATABASE_MANAGEMENT);

		}
				
		private void handleMessage(ByteBuffer buffer, ClientInfo clientInfo) throws ClassNotFoundException, IOException {
			ServerMessage clientMessage = (ServerMessage) ByteUtil.toObject(buffer.array());
			Message<?, ?> protocolMessage = clientMessage.getData();
			protocolMap.get(clientMessage.getKey()).handleMessage(clientInfo, protocolMessage);
		}

		private void addProtocol(Protocol protocol, ProtocolType key) {
			protocolMap.put(key, protocol);
		}
	}
	
	private class ClientInfo {
		private SocketChannel tcpSocketChannel;
		private SocketAddress udpSocketAddress;
		private Connection connection;
		
		public ClientInfo(SocketChannel tcpSocketChannel, Connection connection) {
			this.tcpSocketChannel = tcpSocketChannel;
			this.connection = connection;
		}

		public ClientInfo(SocketAddress udpSocketAddress, Connection connection) {
			this.udpSocketAddress = udpSocketAddress;
			this.connection = connection;
		}

		@Override
		public String toString() {
			return "ClientInfo [tcpSocketChannel=" + tcpSocketChannel + ", udpSocketAddress=" + udpSocketAddress
					+ ", connection=" + connection + "]";
		}

		
		public SocketChannel getTcpSocketChannel() {
			return tcpSocketChannel;
		}
		
	}
}