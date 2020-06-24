package il.co.ilrd.pingpongudp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpPingClient {
    public static void main(String[] args) throws Exception {
		int portNumber = 4444;
		
		DatagramPacket packet = null;
		byte[] buf = new byte[256];
		try (DatagramSocket socket = new DatagramSocket()){	
			for(int i = 0; i <5; ++i) {
				buf = "ping".getBytes();
				packet = new DatagramPacket(buf, buf.length, InetAddress.getByName("255.255.255.255"), portNumber);
				socket.send(packet);
				socket.receive(packet);
				Thread.sleep(500);
				String received = new String(packet.getData(), 0, packet.getLength());
				System.out.println(i + "client received = " + received);
			}
		}
    }
}
