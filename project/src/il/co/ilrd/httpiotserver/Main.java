package il.co.ilrd.httpiotserver;

public class Main {
	public static void main(String[] args) throws Exception {

		//System.out.println(message.toString());
	
		HTTPServer server = new HTTPServer();
//		server.addUdpConnection(50000);
//		server.addTcpConnection(50000);
//		server.addBroadcastConnection(50001);;
//		server.addTcpConnection(55555);
		server.addHTTPConnection(ProtocolPort.DB_HTTP_PORT.getPort());
		
		server.startServer();
		
		Thread.sleep(100000);
		
		server.stopServer();
	}
}
