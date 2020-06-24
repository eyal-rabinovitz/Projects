package il.co.ilrd.selector.pingpongbroadcast;

import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException, InterruptedException {
		//TcpUdpBroadcastPongServer broadcastPongMultiThreaded = new TcpUdpBroadcastPongServer();
		//broadcastPongMultiThreaded.startServers();
		new Thread(new TcpUdpBroadcastPongServer()).start();
		//new TcpUdpBroadcastPongServer().startServers();;
		Thread.sleep(5000);
		
		//TcpPingClient tcpPingClient = new TcpPingClient();
		System.out.println("check if return to main");

	}
}
