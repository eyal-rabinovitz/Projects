package il.co.ilrd.chatserver;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpClient {
    public static void main(String[] args) throws Exception {
		int portNumber = 50000;
		
		DatagramPacket packet = null;
		ServerMessage message = new ServerMessage(ProtocolType.PINGPONG, new PingPongServerMessage("Ping\n"));

		try (DatagramSocket socket = new DatagramSocket()){	
			byte[] buffer = ByteUtil.toByteArray(message);
				packet = new DatagramPacket(buffer, buffer.length, InetAddress.getLocalHost(), portNumber);
				socket.send(packet);
				socket.receive(packet);
				//Thread.sleep(500);
				byte[] returnbuffer = packet.getData();
				message = (ServerMessage) ByteUtil.toObject(returnbuffer);
				System.out.println("client received = " + message.getData());
			
		}
    }
}
