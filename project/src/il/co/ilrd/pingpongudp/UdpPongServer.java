package il.co.ilrd.pingpongudp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpPongServer {
    public static void main(String[] args) throws IOException { 
		int portNumber = 4444;

		byte[] buf = new byte[256]; 
		DatagramPacket packet =  new DatagramPacket(buf, buf.length);
        try(DatagramSocket socket = new DatagramSocket(portNumber);) {
	        for(int i = 0; i <5; ++i) { 
	        	socket.receive(packet); 
				String received = new String(packet.getData(), 0, packet.getLength());
				System.out.println(i + "Server received = " + received);
	        	buf = "ping".getBytes();
				packet = new DatagramPacket(buf, buf.length);
				socket.send(packet);
			}
        }
    }
        
}
