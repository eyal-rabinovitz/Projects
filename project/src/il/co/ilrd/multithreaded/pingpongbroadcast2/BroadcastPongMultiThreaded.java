package il.co.ilrd.multithreaded.pingpongbroadcast2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class BroadcastPongMultiThreaded {

	public void startServer()  {
		new Thread(new UdpServerListener()).start();
		new TcpServerListener().startServer();
	}

	class TcpServerListener {
		private boolean isRunning = true;

		public void startServer() {
			int portNumber = 60000;
			System.out.println("tcp server running on port " + portNumber);
			try (ServerSocket connectionSocket = new ServerSocket(portNumber);) {
				InputDetector exitDetector = 
							new InputDetector(() -> stopTcpServer(connectionSocket), "exit");
				exitDetector.start();
				while (isRunning) {
					new Thread(new TcpRespondThread(connectionSocket.accept())).start();					
				}
			} catch (SocketException e) {
				System.err.println("closing tcp server");

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void stopTcpServer(ServerSocket connectionSocket) {
			isRunning = false;
			try {
				connectionSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		class TcpRespondThread implements Runnable {
			private Socket clientSocket = null;

			public TcpRespondThread(Socket clientSocket) {
				this.clientSocket = clientSocket;
			}

			@Override
			public void run() {
				try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
						BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {
					
					String inputMessage = in.readLine();
					while (inputMessage != null & isRunning) {
						System.err.println("Server received: " + inputMessage);
						out.println("pong");
						inputMessage = in.readLine();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class UdpServerListener implements Runnable {

		private boolean isRunning = true;

		@Override
		public void run() {
			
			int portNumber = 55000;
			System.out.println("udp server running on port " + portNumber);
			byte[] buf = new byte[256];
			DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
			try (DatagramSocket datagramSocket = new DatagramSocket(portNumber);) {

				InputDetector exitDetector = 
							  new InputDetector(() -> {stopUdpServer(datagramSocket);}, "exit");
				exitDetector.start();
				while (isRunning) {
					datagramSocket.receive(datagramPacket);
					respondToClient(datagramPacket, datagramSocket);
				}
			} catch (SocketException e) {
				System.err.println("closing udp server");

			} catch (IOException e) {
				e.printStackTrace();
			} 
		}

		private void stopUdpServer(DatagramSocket datagramSocket) {
			isRunning = false;
			datagramSocket.close();
		}

		private void respondToClient(DatagramPacket datagramPacket, 
									 DatagramSocket datagramSocket) throws IOException {
			printReceivedData(datagramPacket);
			sendResponsePacket(datagramPacket, datagramSocket);
		}

		private void sendResponsePacket(DatagramPacket datagramPacket, 
										DatagramSocket datagramSocket) throws IOException {
			byte[] buf = "pong from server".getBytes();
			datagramPacket.setData(buf);
			datagramSocket.send(datagramPacket);			
		}

		private void printReceivedData(DatagramPacket datagramPacket) {
			String receivedData = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
			System.out.println("Server received: " + receivedData);
		}
	}

}
