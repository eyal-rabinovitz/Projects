package il.co.ilrd.chatserver;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class BroadcastClient {
    public static void main(String[] args) throws Exception {
		int portNumber = 50001;
		
		DatagramPacket packet = null;
		ServerMessage message = new ServerMessage(ProtocolType.PINGPONG, new PingPongServerMessage("Ping\n"));
		try (DatagramSocket socket = new DatagramSocket()){	
			byte[] buffer = ByteUtil.toByteArray(message);
				packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("255.255.255.255"), portNumber);
				socket.send(packet);
				socket.receive(packet);
				//Thread.sleep(500);
				buffer = packet.getData();
				message = (ServerMessage) ByteUtil.toObject(buffer);
				System.out.println("client received = " + message.getData());
			
		}
    }
}