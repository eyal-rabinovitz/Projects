package il.co.ilrd.multithreaded.tcppingpong;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpPongMultiThreaded {

	public static void main(String[] args) {
		TcpPongMultiThreaded tcpPongMultiThreaded = new TcpPongMultiThreaded();
		tcpPongMultiThreaded.TCPPingPong();
	}
	
	private void TCPPingPong() {
	    Runnable serverrRunnable = new Runnable() {
			public void run() {
				TCPServerMethod();
			}
		};
		new Thread(serverrRunnable).start();

		Runnable clientRunnable = new Runnable() {
			public void run() {
				TCPClientMethod();	
			}
		};
		for(int i = 0; i < 2; ++i)
			new Thread(clientRunnable).start();
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
	
	public void TCPClientMethod() {
		try (
			    Socket clientSocket = new Socket("com", 5550);
			    PrintWriter out =
			        new PrintWriter(clientSocket.getOutputStream(), true);
			    BufferedReader in = new BufferedReader(
			        new InputStreamReader(clientSocket.getInputStream()));
			) {
				while(true) {
					out.write("ping\n");
					out.flush();
					System.out.println(in.readLine());
					Thread.sleep(1000);
				}
		} catch (Exception e) {
				System.out.println("client " + e);				
		}
	}
}
