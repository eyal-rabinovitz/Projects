package il.co.ilrd.selector.pingpongbroadcast;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class TcpUdpBroadcastPongServer implements Runnable {
	private boolean toContinue = true;
	private static final int TCP_PORT_NUMBER = 50000;
	private static final int UDP_PORT_NUMBER = 50001;
	private static final int BROADCAST_PORT_NUMBER = 50002;
	private static final int BUF_SIZE = 256;
	private static final int TIMEOUT = 100000;
	private static final String EXIT_PARAM = "exit";
	private static final String DATA_VERSION_1 = "Ping\n";
	private static final String DATA_VERSION_2 = "Pong\n";
	
	private Selector selector;
	private ServerSocketChannel tcpServer;
	private DatagramChannel udpServer;
	private DatagramChannel udpBroadcastserver;
	private List<SocketChannel> TcpSocketChannelList = new LinkedList<>();
	

	@Override
	public void run() {
		new TcpUdpBroadcastPongServer().startServers();
	}
	
	public void startServers() {		
		stopServerWhenTypeExit();

		try {
			selector = Selector.open();
			initTcpserver();
			initUdpserver();
			initUdpBroadcastserver();
			
			while (toContinue) {
				if(0 == selector.select(TIMEOUT)) {
					if(toContinue) {
						System.out.println("waiting for connection");						
					}
					continue;
				}
				
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> iter = selectedKeys.iterator();
				
				while (iter.hasNext()) {
					SelectionKey key = iter.next();
					
					if(!key.isValid()) {
						continue;
					}
					if (key.isAcceptable()) {
						registerTcpClientToSelector();
					}
					if (key.isReadable()) {
						readableHandler(key);
					}
					iter.remove();
				}
			}
		} catch (ClosedSelectorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void initTcpserver() throws IOException {
	    tcpServer = ServerSocketChannel.open();
	    tcpServer.bind(new InetSocketAddress(InetAddress.getLocalHost(), TCP_PORT_NUMBER));
	    tcpServer.configureBlocking(false);
	    tcpServer.register(selector, SelectionKey.OP_ACCEPT);
	}
	
	private void initUdpserver() throws IOException {
	    udpServer = DatagramChannel.open();
	    udpServer.socket().bind(new InetSocketAddress(UDP_PORT_NUMBER));
	    udpServer.configureBlocking(false);
	    udpServer.register(selector, SelectionKey.OP_READ);
	}
	
	private void initUdpBroadcastserver() throws IOException {
		udpBroadcastserver = DatagramChannel.open();
		udpBroadcastserver.socket().bind(new InetSocketAddress(BROADCAST_PORT_NUMBER));
		udpBroadcastserver.configureBlocking(false);
		udpBroadcastserver.register(selector, SelectionKey.OP_READ); 
	}
	
	private void registerTcpClientToSelector() throws IOException {
		  SocketChannel client = tcpServer.accept();
		  
		  client.configureBlocking(false);
		  client.register(selector, SelectionKey.OP_READ);
		  TcpSocketChannelList.add(client);
	}
	
	private void readableHandler(SelectionKey key) throws IOException {
		Channel channel = key.channel();
		  
		  if(TcpSocketChannelList.contains(channel)) {
			  getDataAndSendDataTcpType(channel);
		  }
		  
		  if(channel == udpServer){
			  getDataAndSendDataUdpType(channel);
		  }
		  
		  if(channel == udpBroadcastserver){
			  getDataAndSendDataUdpType(channel);
		  }
	}
	
	private void getDataAndSendDataTcpType(Channel tcpChannel) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(BUF_SIZE);  
		SocketChannel client = (SocketChannel) tcpChannel;
		
		if (-1 == client.read(buffer)) {
			client.close();
			System.out.println("Client closed the connection");
			TcpSocketChannelList.remove(client);
		}
		else {
			System.out.println("Server received = " + new String(buffer.array()));
			buffer = createResponse(buffer);

			while(buffer.hasRemaining()) {
				client.write(buffer);				
			}
		}
	}
	
	private void getDataAndSendDataUdpType(Channel udpChannel) throws IOException {
	    ByteBuffer buffer = ByteBuffer.allocate(BUF_SIZE);
		DatagramChannel client = (DatagramChannel)udpChannel;

		SocketAddress clientAddress = client.receive(buffer);
		if (null != clientAddress) {
			buffer = createResponse(buffer);
			/*check if works*/
			client.send(createResponse(buffer), clientAddress);
		}
	}
	
	private ByteBuffer createResponse(ByteBuffer buffer) {
		String input = new String(buffer.array(),0, buffer.position());
	
		buffer.clear();
		buffer.put(getOutputAccordingToInput(input));
		buffer.flip();
		
		return buffer;
	}
	
	private byte[] getOutputAccordingToInput(String input) {
		if(0 == DATA_VERSION_1.compareTo(input)) {
			return DATA_VERSION_2.getBytes();
		}
		else if(0 == DATA_VERSION_2.compareTo(input)) {
			return DATA_VERSION_1.getBytes();
		}
		
		return "wrong massege, try again.".getBytes();
	}
	
	private void stopServerWhenTypeExit() {
		Runnable stopRunnable = new Runnable() {
			@Override
			public void run() {
				try(BufferedReader reader = 
						new BufferedReader(new InputStreamReader(System.in))) {
					String input = reader.readLine();
					while(!input.equals(EXIT_PARAM)) {
						input = reader.readLine();
					}
					
					toContinue = false;
					tcpServer.close();
					udpServer.close();
					udpBroadcastserver.close();
					for(Closeable socket: TcpSocketChannelList) {
						socket.close();
					}
					
					selector.close();
				} catch (IOException e){
					System.out.println("stop " + e.getStackTrace());
				}
			}
		};
		new Thread(stopRunnable).start();
	}
}