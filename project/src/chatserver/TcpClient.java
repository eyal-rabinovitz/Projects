package chatserver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class TcpClient {

	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {

		int portNumber = 50000;
		
		ServerMessage message1 = new ServerMessage(ProtocolType.PINGPONG, new PingPongServerMessage("Pong\n"));
		ByteBuffer buffer = ByteBuffer.allocate(2048); 
		SocketChannel client = SocketChannel.open(new InetSocketAddress(InetAddress.getLocalHost(), portNumber));

		System.out.println("Client: sending message to server");
		for(int i = 0; i < 6; ++i) {
	    	byte[] array1 = ByteUtil.toByteArray(message1);
	    	buffer = ByteBuffer.wrap(array1);

			client.write(buffer);
	    	buffer.clear();

			client.read(buffer);
			buffer.flip();

			@SuppressWarnings("unchecked")
			Message<Integer, Message<String, Void>> message =
												(Message<Integer, Message<String, Void>>) 
												ByteUtil.toObject(buffer.array());
			System.out.println(message.getData().getKey());
			Thread.sleep(500);
		}   
		client.close();
	}
}