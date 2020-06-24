package http_message;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class HttpServer implements Runnable{
	private boolean isRunning = false;
	private ByteBuffer messageBuffer;
	private Selector selector;
	private static int BUFFER_CAP = 4096;
	private int port = 8080; 
	@Override
	public void run() {
		isRunning = true;
		messageBuffer = ByteBuffer.allocate(BUFFER_CAP);
		System.out.println("started");
		try {
			selector = Selector.open();
			ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.bind(new InetSocketAddress(port));
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			selector.select();
			SocketChannel socketChannel = serverSocketChannel.accept();
			socketChannel.read(messageBuffer);		
			String inString = new String(messageBuffer.array());
			HttpParser httpParser = new HttpParser(inString);
			System.out.println(httpParser.getStartLine().getHttpMethod());
			System.out.println(httpParser.getHeader().getHeader("Accept"));
			System.out.println(httpParser.getBody().getBodyString());
			messageBuffer.clear();
			messageBuffer.put("HTTP/1.1 200 ok/r/n/r/n".getBytes());
			messageBuffer.flip();
			socketChannel.write(messageBuffer);
		} catch (Exception e) {
			if (isRunning) {
				System.out.println("fail");
				throw new RuntimeException(e);
			}
		}
	}
	
	@SuppressWarnings("unused")
	private  void printStringParts(String[] strings) {
		System.out.println("printing parts");
		for (int i = 0; i < strings.length; i++) {
			System.out.println(strings[i]);
		}
		System.out.println("size " + strings.length);
	}
}
