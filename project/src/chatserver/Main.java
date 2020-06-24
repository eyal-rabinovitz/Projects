package chatserver;

import java.io.IOException;

public class Main {
	public static void main(String[] args) throws Exception {
		PingPongServerMessage message = new PingPongServerMessage("eyal");
		
		System.out.println(message.toString());
		
		Server server = new Server();
		server.addUdpConnection(50000);
		server.addTcpConnection(50000);
		server.addBroadcastConnection(50001);;
		server.addTcpConnection(55555);

		server.startServer();
		
		//Thread.sleep(10000);
		
		//server.stopServer();
	}
}
