package il.co.ilrd.multithreaded.pingpongbroadcast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class BroadcastPongMultiThreaded {
	private UDPMultiClientServer udpMultiClientServer = new UDPMultiClientServer();
	private TCPMultiClientServer tcpMultiClientServer = new TCPMultiClientServer();
	private UDPBroadcastServer udpBroadcastServer = new UDPBroadcastServer();
	private boolean toContinueMain = true;
	private static final int PORT_NUMBER = 50000;
	private static final int BUF_SIZE = 256;
	private static final String EXIT_PARAM = "exit";
	
	public void startServers() throws InterruptedException {
		BroadcastPongMultiThreaded server = new BroadcastPongMultiThreaded();
		
		server.tcpMultiClientServer.runServer();
		server.udpMultiClientServer.runServer();
		server.udpBroadcastServer.runServer();

		server.stoppingManagment();
	}
	
	private class TCPMultiClientServer {
		private LinkedBlockingDeque<Socket> socketqueue= new LinkedBlockingDeque<>();
		private LinkedBlockingQueue<Thread> threadsqQueue = new LinkedBlockingQueue<>();
		private ServerSocket serverSocket;
		private boolean toContinueTCP = true;
		
		private void runServer() {
		    Runnable serverrRunnable = new Runnable() {
				public void run() {
					TCPServerMethod();
				}
			};
			new Thread(serverrRunnable).start();
		}
		
		private void closeServer() throws IOException, InterruptedException {
			toContinueTCP = false;
			serverSocket.close();
			for(Socket iterSocket : socketqueue) {
				iterSocket.close();
			}
			for(Thread iterThread : threadsqQueue) {
				iterThread.join();
			}
		}

		private void TCPServerMethod() {
			try(ServerSocket serverSocket = new ServerSocket(PORT_NUMBER)) {
				this.serverSocket = serverSocket;
				while(toContinueTCP) {
					runServerThread(serverSocket.accept()); 
				}
				serverSocket.close();
			} catch (Exception e) {
				if(toContinueTCP) {
					System.out.println(e);				
				}
				toContinueTCP = false;
			}
		}
		
		private void runServerThread(Socket clientSocket) {
			Runnable serverrRunnable = new Runnable() {
				public void run() {
					socketqueue.add(clientSocket);
					try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
							 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {
							
								while(toContinueTCP) {
									System.out.println(in.readLine());
									Thread.sleep(2000);	
									out.write("pong \n");
									out.flush();
								}
						} catch (Exception e) {
							System.out.println(e);
						}
				}
			};
			Thread thread =	new Thread(serverrRunnable);
			threadsqQueue.add(thread);
			thread.start();
		}
	}

	private class UDPMultiClientServer {
		private boolean toContinueUDP = true;
		DatagramSocket serverSocket;
				
		private void runServer() {
		    Runnable serverrRunnable = new Runnable() {
				public void run() {	
					UDPServerMethod();
				}
			};
			new Thread(serverrRunnable).start();
		}
		
		private void closeServer() throws IOException {
			toContinueUDP = false;		
			serverSocket.close();
		}
			
		private void UDPServerMethod() {
			try (DatagramSocket serverSocket = new DatagramSocket(PORT_NUMBER)) {
				this.serverSocket = serverSocket;
				byte[] buf = null;
				while(toContinueUDP) {
					buf = new byte[BUF_SIZE];
					DatagramPacket packet = new DatagramPacket(buf, buf.length);
					serverSocket.receive(packet);
					printReceivedData(packet);		
					sendResponsePacket(packet, serverSocket);
				}
			} catch(Exception e) {
				if(toContinueUDP) {
					System.out.println(e);				
				}				
				toContinueUDP = false;
			}
		}	
	}

	private class UDPBroadcastServer {
		private boolean toContinueUDPBroadcast = true;
		DatagramSocket serverSocket;
				
		private void runServer() {
			Runnable serverrRunnable = new Runnable() {
				public void run() {
					UDPBroadcastServerMethod();
				}
			};
			new Thread(serverrRunnable).start();
		}
		
		private void closeServer() throws IOException {
			toContinueUDPBroadcast = false;		
			serverSocket.close();
		}

		private void UDPBroadcastServerMethod() {
			byte[] buf = new byte[BUF_SIZE];
			try(DatagramSocket socket = new DatagramSocket(5001)) {
				serverSocket = socket;
				while(toContinueUDPBroadcast) {
					DatagramPacket packet = new DatagramPacket(buf, buf.length);
					socket.receive(packet);		
					printReceivedData(packet);		
					sendResponsePacket(packet, socket);
				}
			} catch(Exception e) {
				if(toContinueUDPBroadcast) {
					System.out.println(e);				
				}				
				toContinueUDPBroadcast = false;
			}			
		}
	}

	private void sendResponsePacket(DatagramPacket datagramPacket, 	DatagramSocket datagramSocket) throws IOException {
		byte[] buf = "pong from server".getBytes();
		datagramPacket.setData(buf);
		datagramSocket.send(datagramPacket);			
	}
	
	private void printReceivedData(DatagramPacket datagramPacket) {
		String receivedData = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
		System.out.println("Server received: " + receivedData);
	}
	
	private void stoppingManagment() throws InterruptedException {
		Runnable stopRunnable = new Runnable() {
			public void run() {
				try(BufferedReader input = new BufferedReader(new InputStreamReader(System.in))) {
					while(toContinueMain) {
						if(input.readLine().equals(EXIT_PARAM)) {
							udpMultiClientServer.closeServer();
							tcpMultiClientServer.closeServer();
							udpBroadcastServer.closeServer();
							toContinueMain = false;
						}					
					}
				} catch (IOException | InterruptedException e){
					System.out.println("stop" + e);
				}
			}
		};
		new Thread(stopRunnable).start();
	}
}
