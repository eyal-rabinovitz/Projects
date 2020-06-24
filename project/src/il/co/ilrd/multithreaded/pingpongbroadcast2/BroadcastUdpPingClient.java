package il.co.ilrd.multithreaded.pingpongbroadcast2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class BroadcastUdpPingClient {
	private boolean isRunning = true;
	private long id = Thread.currentThread().getId();
	
	public void startClient() throws IOException, IOException {

		int portNumber = 55000;
		DatagramPacket datagramPacket = null;
		byte[] reqBuf = new byte[256];
		
		try (DatagramSocket datagramSocket = new DatagramSocket()) {
			datagramPacket = new DatagramPacket(reqBuf, reqBuf.length, InetAddress.getByName("255.255.255.255"),portNumber);
			datagramSocket.setBroadcast(true);
			InputDetector exitDetector  = new InputDetector(() -> stopUdpClient(datagramSocket), "exit");
			exitDetector.start();
			while (isRunning) {
				sendData(datagramSocket, datagramPacket);
				datagramSocket.receive(datagramPacket);
				printReceivedData(datagramPacket);
			}

		}catch (SocketException e) {
			System.out.println("closing broadcast client");

		}

	}
	private void sendData(DatagramSocket datagramSocket, DatagramPacket packet) throws IOException {
		byte[] reqBuf = new byte[256];
		reqBuf = "ping From Broadcast Client".getBytes();
		packet.setData(reqBuf);
		datagramSocket.send(packet);
	}

	private void printReceivedData(DatagramPacket datagramPacket) {
		String receivedData = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
		System.out.println("Client Broadcast " + id + "received: " + receivedData);
	}
	private void stopUdpClient(DatagramSocket datagramSocket) {
		isRunning = false;
		datagramSocket.close();
	}
}
