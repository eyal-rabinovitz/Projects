package il.co.ilrd.pingpongbroadcast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class BroadcastPongServer {
    public static void main(String[] args) throws Exception {
		int portNumber = 4444;

		try (DatagramSocket socket = new DatagramSocket(portNumber)){
			for(int i = 0; i <5; ++i) {
				byte[] buf = new byte[256];
				DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName("255.255.255.255"), portNumber);
				socket.receive(packet);
				Thread.sleep(500);
				String received = new String(packet.getData(), 0, packet.getLength());
				System.out.println("			" + i + "server received = " + received);
				buf = "pong".getBytes();
				packet.setData(buf);
				socket.send(packet);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
    }
}
