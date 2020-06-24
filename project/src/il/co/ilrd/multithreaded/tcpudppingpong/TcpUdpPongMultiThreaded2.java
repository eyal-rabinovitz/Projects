package il.co.ilrd.multithreaded.tcpudppingpong;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class TcpUdpPongMultiThreaded2 {
	public static void main(String[] args) {
		TcpServer tcpServer = new TcpServer();
		new Thread(tcpServer).start();
		
		DatagramPacket packet = null;
		try(DatagramSocket socket = new DatagramSocket(4444);){
			while(true) {
				byte[] data = new byte[256];
				packet = new DatagramPacket(data, data.length);
				System.out.println("UdpServer ready");
				socket.receive(packet);
				
				String received = new String(packet.getData(), 0, packet.getLength());
				System.out.println("Server received = " + received);
				data = "ping".getBytes();
				packet.setData(data);
				socket.send(packet);

			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static class TcpServer implements Runnable {
		@Override
		public void run() {
			try(ServerSocket serverSocket = new ServerSocket(4444);){
				while(true) {
					System.out.println("TcpServer ready");
					Socket clientSocket = serverSocket.accept();
					TcpClientHandler clientHandler = new TcpClientHandler(clientSocket);
					new Thread(clientHandler).start();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private static class TcpClientHandler implements Runnable {
		private final Socket clientSocket;
		
		public TcpClientHandler(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}
		@Override
		public void run() {
			try(PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
					BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {
				String inputLine = in.readLine();
				while(null != inputLine) {
					Thread.sleep(500);
					out.print("pong \n");
					out.flush();
					System.out.println("server received = " + inputLine);
					inputLine = in.readLine();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
