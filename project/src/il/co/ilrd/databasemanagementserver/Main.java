package il.co.ilrd.databasemanagementserver;

public class Main {
	public static void main(String[] args) throws Exception {

		//System.out.println(message.toString());
	
		IOTServer server = new IOTServer();
//		server.addUdpConnection(50000);
//		server.addTcpConnection(50000);
//		server.addBroadcastConnection(50001);;
//		server.addTcpConnection(55555);
		server.addTcpConnection(60000);

		server.startServer();
		
		Thread.sleep(100000);
		
		server.stopServer();
	}
}
