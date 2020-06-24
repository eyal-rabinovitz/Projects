package il.co.ilrd.multithreaded.pingpongbroadcast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingDeque;

public class TcpPingClient {
	private LinkedBlockingDeque<Socket> socketQueue= new LinkedBlockingDeque<>();
	private boolean toContinue = true;
	private int waitTIme = 5000;

	public static void main(String[] args) {
		TcpPingClient tcpClient = new TcpPingClient();
		tcpClient.runTCPClient();
		tcpClient.stoppingThread();
	}
	
	public void runTCPClient() {
		Runnable clientRunnable = new Runnable() {
			public void run() {
				TCPClientMethod();	
			}
		};
		for(int i = 0; i < 1; ++i)
			new Thread(clientRunnable).start();
	}
		
	public void TCPClientMethod() {
		try (
			    Socket clientSocket = new Socket(InetAddress.getLocalHost().getHostName(), 5000);
				PrintWriter out =
			        new PrintWriter(clientSocket.getOutputStream(), true);
			    BufferedReader in = new BufferedReader(
			        new InputStreamReader(clientSocket.getInputStream()));
			) {
				socketQueue.add(clientSocket);
				while(toContinue) {
					out.write("ping\n");
					out.flush();
					System.out.println(in.readLine());
					Thread.sleep(waitTIme);
				}
				clientSocket.close();
				out.close();
				in.close();
		} catch (Exception e) {
			if(toContinue) {
				System.out.println(e);				
			}
			toContinue = false;		}
	}
	
	public void stoppingThread() {
		Runnable stopRunnable = new Runnable() {
			public void run() {
				try(BufferedReader input = new BufferedReader(new InputStreamReader(System.in))) {
					while(toContinue) {
						if(input.readLine().equals("exit")) {
							System.out.println("exit");
							toContinue = false;					
							for(Socket iterSocket : socketQueue) {
								iterSocket.close();
							}
						}					
					}
				} catch (IOException e){
					System.out.println("stop" + e);
				}
			}
		};
		new Thread(stopRunnable).start();
	}
}
