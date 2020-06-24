package il.co.ilrd.selector.pingpongbroadcast;
//package il.co.ilrd.selector.pingpongbroadcast;
//
//import java.io.BufferedReader;
//import java.io.Closeable;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.InetAddress;
//import java.net.InetSocketAddress;
//import java.net.SocketAddress;
//import java.nio.ByteBuffer;
//import java.nio.channels.Channel;
//import java.nio.channels.ClosedSelectorException;
//import java.nio.channels.DatagramChannel;
//import java.nio.channels.SelectionKey;
//import java.nio.channels.Selector;
//import java.nio.channels.ServerSocketChannel;
//import java.nio.channels.SocketChannel;
//import java.util.Iterator;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Set;
//
//public class ServerSelector {
//	private final static int PORT_NUM_UDP = 50000;
//	private final static int PORT_NUM_TCP = 60000;
//	private final static int PORT_NUM_BRO = 55000;
//	private Selector selector;
//	private ServerSocketChannel tcpSocket;
//	private DatagramChannel udpSocket;
//	private DatagramChannel broadcastSocket;
//	private List<Closeable> socketList = new LinkedList<>();
//	ByteBuffer buffer = ByteBuffer.allocate(256);
//
//	public void startServer() {
//		InputDetector exitDetector = new InputDetector();
//		new Thread(exitDetector).start();
//		
//		try {
//			selector = Selector.open();
//			initTcpServer();
//			initUdpServer();
//			initBroadcastServer();
//			
//			while (true) {
//				selector.select();
//		        Set<SelectionKey> selectedKeys = selector.selectedKeys();
//		        Iterator<SelectionKey> iter = selectedKeys.iterator();
//
//		        while (iter.hasNext()) {
//		        	SelectionKey key = iter.next();
//		 
//		        	if (key.isAcceptable()) {
//		        		registerTcpClient(selector, tcpSocket);
//		            }
//
//		        	if (key.isReadable()) {
//		        		Channel channel = key.channel();
//		        		if(channel == broadcastSocket) {
//		        			udpHandler(key, "broadcastSocket");
//		        		}
//
//		        		else if(channel == udpSocket) {
//		        			udpHandler(key, "udp");
//		        		}
//		        	
//		        		else{
//		        			SocketChannel client = (SocketChannel) key.channel();
//		        			String input = tcpReadData(client);
//		        			if(null == input) {
//			        			iter.remove();			        			
//			        			continue;
//			        		}
//		        			buffer.put(getMessageToSend(input).getBytes());    
//		        			tcpSendData(client);
//		        		}
//		        	}
//		        	
//		        	iter.remove();
//		        }
//			}
//		} catch (ClosedSelectorException e1) {
//			return;
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}		
//	}
//	
//	private void udpHandler(SelectionKey key, String serverType) throws IOException {
//		DatagramChannel channel = (DatagramChannel) key.channel();
//		SocketAddress clientAddress = channel.receive(buffer);
//		String input = udpReadData(serverType);
//		udpSendData(channel, clientAddress, input);
//	}
//	
//	private void initTcpServer() throws IOException {
//		tcpSocket = ServerSocketChannel.open();
//		tcpSocket.bind(new InetSocketAddress(InetAddress.getLocalHost(), PORT_NUM_TCP));
//		tcpSocket.configureBlocking(false);
//		tcpSocket.register(selector, SelectionKey.OP_ACCEPT);
//		socketList.add(tcpSocket);
//	}
//	
//	private void initUdpServer() throws IOException {
//		udpSocket = DatagramChannel.open();
//		udpSocket.socket().bind(new InetSocketAddress(PORT_NUM_UDP));
//		udpSocket.configureBlocking(false);
//		udpSocket.register(selector, SelectionKey.OP_READ);
//		socketList.add(udpSocket);
//	}
//	
//	private void initBroadcastServer() throws IOException {
//		broadcastSocket = DatagramChannel.open();
//		broadcastSocket.socket().bind(new InetSocketAddress(PORT_NUM_BRO));
//		broadcastSocket.configureBlocking(false);
//		broadcastSocket.register(selector, SelectionKey.OP_READ);
//		socketList.add(broadcastSocket);
//	}
//	
//	private void registerTcpClient(Selector selector, ServerSocketChannel tcpSocket) throws IOException {
//		SocketChannel client = tcpSocket.accept();
//		socketList.add(client);
//		client.configureBlocking(false);
//		client.register(selector, SelectionKey.OP_READ);
//	}
//
//	private String getMessageToSend(String input) {
//		if(input.equals("ping")) {
//   	    	return "pong";
//   	    }
//   	    else if (input.equals("pong")) {
//   	    	return "ping";
//   	    }
//		
//   	    return "wrong data";
//	}
//	
//	private void stopServer() throws IOException {
//		selector.close();	
//		
//		for(Closeable socket: socketList) {
//			socket.close();
//		}
//	}
//	
//    private String tcpReadData(SocketChannel client) throws IOException {
//    	int bytes = client.read(buffer);
//    	if (-1 == bytes) {
//    		client.close();
//    		return null;
//    	}
//    	
//        String input = new String(buffer.array()).trim();
//        buffer.clear();
//    	System.out.println("Tcp server received:" + input);
//    	
//    	return input;
//    }
//    
//    private void tcpSendData(SocketChannel client) throws IOException {
//         buffer.flip();
//         client.write(buffer);
//         buffer.clear();
//    }
//    
//	private String udpReadData(String str) {
//		 String input = new String(buffer.array(), 0, buffer.position());
//		 System.out.println(str + "server received:" + input);
//		 buffer.clear();
//		 
//		 return input;
//	}
//	
//	private void udpSendData(DatagramChannel channel, SocketAddress clientAddress,
//								String input) throws IOException {
//		buffer.put(getMessageToSend(input).getBytes());
//		buffer.flip();
//		channel.send(buffer, clientAddress);
//		buffer.clear();
//}
//
///********************************************************************************************/    
//	private class InputDetector implements Runnable {
//		@Override
//		public void run() {
//			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//			String input;
//			
//			try {
//				input = reader.readLine();
//				while(!input.equals("exit")) {
//					input = reader.readLine();
//				}
//				
//				stopServer();
//				
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}	
//	} 
//}
