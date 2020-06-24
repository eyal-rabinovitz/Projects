package databasemanagementserver;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

public class Tadiran {

	public static void main(String[] args) throws Exception {
		new Tadiran("tadiran").startClient();

	}

	private String companyName;
	ServerMessage serverMessage = null;
	ByteBuffer buffer = ByteBuffer.allocate(4096);
	SocketChannel clientSocket = null;
	ServerMessage receivedMssage = null;

	public Tadiran(String companyName) {
		this.companyName = companyName;
	}

	public void startClient() throws Exception {
		String hostName = "127.0.0.1";
		int portNumber = ProtocolPort.DATABASE_MANAGEMENT_PORT.getPort();
		System.out.println(companyName + " Connecting to DB server " + hostName + " in port " + portNumber);

		try {
			clientSocket = SocketChannel.open(new InetSocketAddress(InetAddress.getLocalHost(), portNumber));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		receieveMessageFromServer();

		// send create DB message
		prepareAndSendMessage(DatabaseKeys.CREATE_COMPANY_DATABASE, "jibrish");

//
//		// invalid key
		prepareAndSendMessage(DatabaseKeys.CREATE_TABLE, 
					  "CREATE TABLE TestCountry (country_id int AUTO_INCREMENT PRIMARY KEY, country_name varchar(20) NOT NULL UNIQUE)");

//		// send create table message
		prepareAndSendMessage(DatabaseKeys.CREATE_TABLE, 
				  "CREATE TABLE IOTEvent (\r\n" + 
				  "    iot_event_id int AUTO_INCREMENT PRIMARY KEY,\r\n" + 
				  "	serial_number varchar(16) NOT NULL,\r\n" + 
				  "    description varchar(255) NOT NULL,\r\n" + 
				  "    event_timestamp TimeStamp DEFAULT CURRENT_TIMESTAMP\r\n" + 
				  ");");
		// send create row message
		prepareAndSendMessage(DatabaseKeys.CREATE_ROW,
				"INSERT INTO TestCountry VALUES (NULL, 'England'),(NULL, 'Jordan')");
//		
//		// send read row message
		prepareAndSendMessage(DatabaseKeys.READ_ROW, "TestCountry", "country_id", 1);
//		
//		// send read field by index message
		System.out.println("1");
		prepareAndSendMessage(DatabaseKeys.READ_FIELD_BY_INDEX, "TestCountry",  "country_id", 20, 20);
		System.out.println("2");
//		 send create iot message
		prepareAndSendMessage(DatabaseKeys.CREATE_IOT_EVENT, "00001|'Not Working'|CURRENT_TIMESTAMP");

		// send read and update field by index message
		prepareAndSendMessage(DatabaseKeys.READ_FIELD_BY_INDEX, "IOTEvent", "iot_event_id", 1, 3);
		prepareAndSendMessage(DatabaseKeys.UPDATE_FIELD_BY_INDEX, "IOTEvent", "iot_event_id", 1, 3, "WORKING!!!!!");
		prepareAndSendMessage(DatabaseKeys.READ_FIELD_BY_INDEX, "IOTEvent", "iot_event_id", 1, 3);

		// send read and update field by Column name message
		prepareAndSendMessage(DatabaseKeys.UPDATE_FIELD_BY_NAME, "IOTEvent", "iot_event_id", 1, "description","'er descr !!!!!'");
		prepareAndSendMessage(DatabaseKeys.READ_FIELD_BY_NAME, "IOTEvent", "iot_event_id", 1, "description");

		// send delete row
		//prepareAndSendMessage(DatabaseKeys.DELETE_ROW, "IOTEvent", "iot_event_id", 1);

		// send delete table
		//prepareAndSendMessage(DatabaseKeys.DELETE_TABLE, "TestCountry");

	}

	private void prepareAndSendMessage(DatabaseKeys databaseKey, Object... objects) throws Exception {
		List<Object> params = new ArrayList<>();
		addToParamsList(params, objects);
		sendMessage(new DatabaseManagementMessage(new ActionTypeKey(companyName, databaseKey), params));
		Thread.sleep(2000);
	}

	private void receieveMessageFromServer() throws IOException, ClassNotFoundException {
		new Thread(new Runnable() {

			@Override
			public void run() {
				ByteBuffer receiveBuffer = ByteBuffer.allocate(4096);
				while (true) {
					receiveBuffer.clear();
					try {
						clientSocket.read(receiveBuffer);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						receivedMssage = (ServerMessage) ByteUtil.toObject(receiveBuffer.array());
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					DatabaseManagementMessage dBreceivedMessage = (DatabaseManagementMessage) receivedMssage.getData();

					System.out.println("response:" + dBreceivedMessage.getKey().getDatabaseName());
					System.out.println("response:" + dBreceivedMessage.getKey().getActionType());
					List<Object> response = dBreceivedMessage.getData();
					for (Object object : response) {
						System.out.println("response: " + object);

					}
				}
			}
		}).start();
		;

	}

	private void addToParamsList(List<Object> params, Object... objects) {
		params.clear();
		for (Object obj : objects) {
			params.add(obj);
		}
	}

	private void sendMessage(DatabaseManagementMessage dBMessage) throws IOException {
		serverMessage = new ServerMessage(ProtocolType.DATABASE_MANAGEMENT, dBMessage);
		byte[] serverMessageArray = ByteUtil.toByteArray(serverMessage);
		buffer.clear();
		buffer.put(serverMessageArray);
		buffer.flip();
		clientSocket.write(buffer);
	}

}