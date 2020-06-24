package il.co.ilrd.selector.pingpongbroadcast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpPingClient {
    public static void main(String[] args) throws Exception {
		int portNumber = 50000;
		
		DatagramPacket packet = null;
		byte[] buf = new byte[256];
		try (DatagramSocket socket = new DatagramSocket()){	
			for(int i = 0; i < 6; ++i) {
				buf = "Ping\n".getBytes();
				packet = new DatagramPacket(buf, buf.length, InetAddress.getLocalHost(), portNumber);
				socket.send(packet);
				socket.receive(packet);
				Thread.sleep(500);
				String received = new String(packet.getData(), 0, packet.getLength());
				System.out.println("client received = " + received);
			}
		}
    }
}
