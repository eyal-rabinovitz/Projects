package il.co.ilrd.multithreaded.pingpongbroadcast2;

import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException, InterruptedException {
		BroadcastPongMultiThreaded broadcastPongMultiThreaded = new BroadcastPongMultiThreaded();
		broadcastPongMultiThreaded.startServer();
		
		Thread.sleep(1000);
		
		BroadcastUdpPingClient broadcastPingClient = new BroadcastUdpPingClient();
		broadcastPingClient.startClient();
		//broadcastPingClient.startClient();

	}
}
