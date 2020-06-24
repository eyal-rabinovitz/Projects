package il.co.ilrd.multithreaded.tcpudppingpong;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import il.co.ilrd.multithreaded.tcppingpong.TcpPongMultiThreaded;

public class TcpUdpPongMultiThreaded {

	public static void main(String[] args) {
		TcpUdpPongMultiThreaded tcpUdpPongMultiThreaded = new TcpUdpPongMultiThreaded();
		tcpUdpPongMultiThreaded.TCPUDPPingPong("TCP");
		tcpUdpPongMultiThreaded.TCPUDPPingPong("UDP");
	}

	private void TCPUDPPingPong(String protocol) {
	    Runnable serverrRunnable = new Runnable() {
			public void run() {
				if("TCP" == protocol)
					new TcpPongMultiThreaded().TCPServerMethod();
				else if("UDP" == protocol)
					UDPServerMethod();
			}
		};
		new Thread(serverrRunnable).start();

		Runnable clientRunnable = new Runnable() {
			public void run() {
				if("TCP" == protocol)
					new TcpPongMultiThreaded().TCPClientMethod();
				else if("UDP" == protocol)	
					UDPClientMethod();	
			}
		};
		new Thread(clientRunnable).start();
	}
		
	private void UDPServerMethod() {
		try (DatagramSocket serverSocket = new DatagramSocket(5550)) {
			while(true) {
				byte[] buf = new byte[20];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				serverSocket.receive(packet);
				System.out.println(new String(buf));
				Thread.sleep(1000);
				buf = "pongUDP".getBytes();
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                serverSocket.send(packet);
			}
		} catch (Exception e) {
			System.out.println("server " + e);
		}
	}
	
	public void TCPServerMethod() {
		try(ServerSocket serverSocket = new ServerSocket(5550)) {
			while(true) {
				runServerThread(serverSocket.accept()); 
			}		
		} catch (Exception e) {
			System.out.println("server " + e);
		}
	}
	
	private void runServerThread(Socket clientSocket) {
		Runnable serverrRunnable = new Runnable() {
			public void run() {
				TCPServerThreadMethod(clientSocket); 
			}
		};
		new Thread(serverrRunnable).start();
	}
	
	private void TCPServerThreadMethod(Socket clientSocket) {
		try ( 
			    PrintWriter out =
			        new PrintWriter(clientSocket.getOutputStream(), true);
			    BufferedReader in = new BufferedReader(
			        new InputStreamReader(clientSocket.getInputStream()));
			) {
				while(true) {
					System.out.println(in.readLine());
					Thread.sleep(1000);	
					out.write("pong\n");
					out.flush();
				}
		} catch (Exception e) {
			System.out.println("server " + e);

		}
	}
	private void UDPClientMethod() {
		try (DatagramSocket socket = new DatagramSocket()) {
				while(true) {
			        byte[] buf = new byte[20];
			        buf = "pingUDP".getBytes();
			        InetAddress address = InetAddress.getByName("com");
			        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 5550);
			        socket.send(packet);
			        
			        packet = new DatagramPacket(buf, buf.length);
			        socket.receive(packet);
			        System.out.println(new String(buf));
					Thread.sleep(1000);
				}
		} catch (Exception e) {
				System.out.println("client " + e);				
		}
	}
}
