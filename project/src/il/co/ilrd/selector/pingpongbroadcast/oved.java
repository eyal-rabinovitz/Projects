package il.co.ilrd.selector.pingpongbroadcast;
//package il.co.ilrd.selector.pingpongtcpudp;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.InetAddress;
//import java.net.InetSocketAddress;
//import java.net.SocketAddress;
//import java.nio.ByteBuffer;
//import java.nio.channels.Channel;
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
//public class TcpUdpPongServer implements Runnable{
//	private ServerSocketChannel	tcpserverSocket;
//	private DatagramChannel udpServerDatagram;
//	private Selector selector;
//	private ByteBuffer dataBuffer = ByteBuffer.allocate(BUFFER_SIZE);
//	private boolean toContinue = true;
//	private List<SocketChannel> listTcpSoket = new LinkedList<>();
//	
//	private final static int PORT = 50000;
//	private final static int BUFFER_SIZE = 100;
//	private final static String PING = "Ping";
//	private final static String PONG = "Pong";
//	
//	public static void main(String[] args) {
//		TcpUdpPongServer server = new TcpUdpPongServer();
//		new Thread(server).start();
//	}
//
//	@Override
//	public void run() {
//		try {
//			System.out.println("server");
//			runServer();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	private void runServer() throws IOException {
//		stopServerWhenTypeExit();
//		
//		selector = Selector.open();
//		initTcpserverSocketAndRegister();
//		initUdpserverSocketAndRegister();
//		
//		while (toContinue) {
//			selector.select();
//			Set<SelectionKey> selectedKeys = selector.selectedKeys();
//			Iterator<SelectionKey> iter = selectedKeys.iterator();
//			
//			while (iter.hasNext()) {
//				SelectionKey key = iter.next();
//				
//				if(!key.isValid()) {
//					continue;
//				}
//				if (key.isAcceptable()) {
//					registerTcpClientToSelector();
//				}
//				else if (key.isReadable()) {
//					readableHanler(key);
//				}
//				iter.remove();
//			}
//		}
//	}
//	
//	private void readableHanler(SelectionKey key) throws IOException {
//		Channel currChannel = key.channel();
//		
//		if(listTcpSoket.contains(currChannel)) {
//			getPingAndSendPongTcp(currChannel);
//		}
//		else if(currChannel == udpServerDatagram) {
//			getPingAndSendPongUdp(currChannel);
//		}
//	}
//	
//	private void initTcpserverSocketAndRegister() throws IOException {
//		tcpserverSocket = ServerSocketChannel.open();
//		tcpserverSocket.bind(new InetSocketAddress(InetAddress.getLocalHost(),
//																		PORT));
//		tcpserverSocket.configureBlocking(false);
//		tcpserverSocket.register(selector, SelectionKey.OP_ACCEPT);
//	}
//	
//	private void initUdpserverSocketAndRegister() throws IOException {
//		udpServerDatagram = DatagramChannel.open();
//		udpServerDatagram.socket().bind(new InetSocketAddress(PORT));
//		udpServerDatagram.configureBlocking(false);
//		udpServerDatagram.register(selector, SelectionKey.OP_READ);
//	}
//	
//	private void registerTcpClientToSelector() throws IOException {
//		SocketChannel clientTcp = tcpserverSocket.accept();
//		clientTcp.configureBlocking(false);
//		clientTcp.register(selector, SelectionKey.OP_READ);
//		listTcpSoket.add(clientTcp);
//	}
//	
//	private void getPingAndSendPongTcp(Channel tcpChannel) 
//															throws IOException {
//		SocketChannel client = (SocketChannel)tcpChannel;
//		
//		if(-1 == client.read(dataBuffer)) {
//			client.close();
//			System.out.println("Not accepting client messages anymore");
//		} 
//		else {
//			String input = convertInputToStr();
//			System.out.println(input);
//			
//			putOutputInBuffer(input);
//			client.write(dataBuffer);
//			dataBuffer.clear();
//		}
//	}
//	
//	private void getPingAndSendPongUdp(Channel udpChannel) 
//												 		throws IOException {
//			DatagramChannel client = (DatagramChannel)udpChannel;
//			SocketAddress clientAddress = client.receive(dataBuffer);
//			String input = convertInputToStr();
//			System.out.println(input);
//			
//			putOutputInBuffer(input);
//			client.send(dataBuffer, clientAddress);
//			dataBuffer.clear();
//	}
//	
//	private void putOutputInBuffer(String input) {
//		byte[] buf = getOutputAccordingInput(input);
//		dataBuffer.clear();
//		dataBuffer.put(buf);
//		dataBuffer.flip();
//	}
//
//	private byte[] getOutputAccordingInput(String input) {
//		if(0 == PING.compareTo(input)) {
//			return "pong\n".getBytes();
//		}
//		else if(0 == PONG.compareTo(input)) {
//			return "ping\n".getBytes();
//		}
//		
//		return "wrong massege, try again.".getBytes();
//	}
//	
//	private String convertInputToStr() {
//		return new String(dataBuffer.array(),0, dataBuffer.position());
//	}
//	
//	private void stopServerWhenTypeExit() {
//		Runnable stopRunnable = new Runnable() {
//			@Override
//			public void run() {
//				try(BufferedReader input = 
//						new BufferedReader(new InputStreamReader(System.in))) {
//					while(!input.readLine().equals("exit")) {					
//					}
//					
//					System.out.println("exit");
//					tcpserverSocket.close();
//					udpServerDatagram.close();
//					selector.close();
//					toContinue = false;
//				
//				} catch (IOException e){
//					System.out.println("stop" + e);
//				}
//			}
//		};
//		new Thread(stopRunnable).start();
//	}
//}