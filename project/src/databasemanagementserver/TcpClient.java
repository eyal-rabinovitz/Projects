package databasemanagementserver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

public class TcpClient {

	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
		int portNumber = 60000;

		String url = "localhost:3306/";
		String databaseName = "DatabaseManagementExample";
		String user = "root";
		String password = "132435";
		
		List<Object> params = new ArrayList<>();
		/* (String url, String userName, String password, String databaseName) */
		params.add(databaseName);

		ActionTypeKey actionTypeKey = new ActionTypeKey(databaseName, DatabaseKeys.CREATE_COMPANY_DATABASE);
		DatabaseManagementMessage databaseMessage = new DatabaseManagementMessage(actionTypeKey, params);
		ServerMessage message1 = new ServerMessage(ProtocolType.DATABASE_MANAGEMENT, databaseMessage);
		
		ByteBuffer buffer = ByteBuffer.allocate(2048); 

		SocketChannel client = SocketChannel.open(new InetSocketAddress(InetAddress.getLocalHost(), portNumber));
		
		System.out.println("Client: sending message to server");
    	byte[] array1 = ByteUtil.toByteArray(message1);
    	//buffer = ByteBuffer.wrap(array1);
    	buffer.put(array1);
		client.write(buffer);
    	buffer.clear();

		client.read(buffer);
		buffer.flip();

		@SuppressWarnings("unchecked")
		Message<Integer, Message<ActionTypeKey, List<Object>>> message = (Message<Integer, Message<ActionTypeKey, List<Object>>>)
																ByteUtil.toObject(buffer.array());
		System.out.println(message.getData().getKey().getDatabaseName());
		System.out.println(message.getData().getKey().getActionType());

		System.out.println(message.getData().getData());

		Thread.sleep(500);

		/**/
		List<Object> params2 = new ArrayList<>();
		params2.add("CREATE TABLE checking ("
				+ "ID int UNIQUE NOT NULL,"
				+ "number int );" );
		
		actionTypeKey = new ActionTypeKey(databaseName, DatabaseKeys.CREATE_TABLE);
		databaseMessage = new DatabaseManagementMessage(actionTypeKey, params2);
		message1 = new ServerMessage(ProtocolType.DATABASE_MANAGEMENT, databaseMessage);
		
		client = SocketChannel.open(new InetSocketAddress(InetAddress.getLocalHost(), portNumber));

		System.out.println("Client: sending message to server");
    	array1 = ByteUtil.toByteArray(message1);
    	buffer = ByteBuffer.wrap(array1);

		client.write(buffer);
    	buffer.clear();

		client.read(buffer);
		buffer.flip();

		@SuppressWarnings("unchecked")
		Message<Integer, Message<ActionTypeKey, List<Object>>> message2 = (Message<Integer, Message<ActionTypeKey, List<Object>>>)
																ByteUtil.toObject(buffer.array());
		System.out.println(message2.getData().getKey().getDatabaseName());
		System.out.println(message2.getData().getKey().getActionType());

		System.out.println(message2.getData().getData());

		Thread.sleep(500);
		/**/

		
		/**/
		List<Object> params3 = new ArrayList<>();
		params2.add("CREATE TABLE checking ("
				+ "ID int UNIQUE NOT NULL,"
				+ "number int );" );
		
		actionTypeKey = new ActionTypeKey(databaseName, DatabaseKeys.CREATE_TABLE);
		databaseMessage = new DatabaseManagementMessage(actionTypeKey, params3);
		message1 = new ServerMessage(ProtocolType.DATABASE_MANAGEMENT, databaseMessage);
		
		client = SocketChannel.open(new InetSocketAddress(InetAddress.getLocalHost(), portNumber));

		System.out.println("Client: sending message to server");
    	array1 = ByteUtil.toByteArray(message1);
    	buffer = ByteBuffer.wrap(array1);

		client.write(buffer);
    	buffer.clear();

		client.read(buffer);
		buffer.flip();

		@SuppressWarnings("unchecked")
		Message<Integer, Message<ActionTypeKey, List<Object>>> message3 = (Message<Integer, Message<ActionTypeKey, List<Object>>>)
																ByteUtil.toObject(buffer.array());
		System.out.println(message3.getData().getKey().getDatabaseName());
		System.out.println(message3.getData().getKey().getActionType());

		System.out.println(message3.getData().getData());

		Thread.sleep(500);
		/**/
		
		
		client.close();
	}
}


