package il.co.ilrd.chatserver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class TcpClientChatServer2 {
	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {

		int portNumber = 55555;
		ServerMessage message = new ServerMessage(ProtocolType.CHAT_SERVER, new ChatServerMessage(ChatProtocolKeys.REGISTRATION_REQUEST, "eyal2"));

		ByteBuffer buffer = ByteBuffer.allocate(2048); 
		SocketChannel client = SocketChannel.open(new InetSocketAddress(InetAddress.getLocalHost(), portNumber));
		boolean isRun = true;
		System.out.println("Client: sending message to server");
		byte[] array1 = ByteUtil.toByteArray(message);
		
		buffer.put(array1);
		buffer.flip();
		client.write(buffer);
		buffer.clear();
		client.read(buffer);

		ServerMessage receivedmessage =	(ServerMessage)ByteUtil.toObject(buffer.array());
		System.out.println(receivedmessage.getData().toString());
		
		Thread.sleep(500);
		buffer.clear();

		message = new ServerMessage(ProtocolType.CHAT_SERVER, new ChatServerMessage(ChatProtocolKeys.MESSAGE, "hi to every one"));
		
		array1 = ByteUtil.toByteArray(message);
		
		buffer.put(array1);
		buffer.flip();
		client.write(buffer);
		buffer.clear();
		client.read(buffer);

		receivedmessage =	(ServerMessage)ByteUtil.toObject(buffer.array());
		System.out.println(receivedmessage.getData().toString());
		
		
		buffer.clear();
		System.out.println("1");

		client.read(buffer);
		receivedmessage =	(ServerMessage)ByteUtil.toObject(buffer.array());
		System.out.println(receivedmessage.getData().toString());
		
		
		buffer.clear();
		System.out.println("2");

		client.read(buffer);
		receivedmessage =	(ServerMessage)ByteUtil.toObject(buffer.array());
		System.out.println(receivedmessage.getData().toString());
		
		
		buffer.clear();
		System.out.println("3");

		client.read(buffer);
		receivedmessage =	(ServerMessage)ByteUtil.toObject(buffer.array());
		System.out.println(receivedmessage.getData().toString());
		
		buffer.clear();
		System.out.println("4");

		client.read(buffer);
		receivedmessage =	(ServerMessage)ByteUtil.toObject(buffer.array());
		System.out.println(receivedmessage.getData().toString());
		
		
		buffer.clear();
		System.out.println("5");

		client.read(buffer);
		receivedmessage =	(ServerMessage)ByteUtil.toObject(buffer.array());
		System.out.println(receivedmessage.getData().toString());
		
		
		buffer.clear();
		System.out.println("6");

		client.read(buffer);
		receivedmessage =	(ServerMessage)ByteUtil.toObject(buffer.array());
		System.out.println(receivedmessage.getData().toString());
		/*while(isRun) {

			@SuppressWarnings("unchecked")
			if(receivedmessage.getData().getKey().equals("exit")) {
				isRun = false;
			}
			Thread.sleep(500);
			isRun = false;
		} */  
		//client.close();
	}
}
