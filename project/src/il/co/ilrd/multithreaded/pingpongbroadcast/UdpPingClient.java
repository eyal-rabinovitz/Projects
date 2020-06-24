package il.co.ilrd.multithreaded.pingpongbroadcast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.LinkedBlockingDeque;

public class UdpPingClient {
	private LinkedBlockingDeque<DatagramSocket> socketQueue= new LinkedBlockingDeque<>();
	private boolean toContinue = true;
	private int waitTIme = 5000;

	public static void main(String[] args) {
		UdpPingClient udpClient = new UdpPingClient();
		udpClient.runUDPClient();
		udpClient.stoppingThread();
	}
	
	private void runUDPClient() {
		Runnable clientRunnable = new Runnable() {
			public void run() {
				UDPClientMethod();	
			}

		};
		for(int i = 0; i < 1; ++i)
			new Thread(clientRunnable).start();		
	}
	
	private void UDPClientMethod() {
		try (DatagramSocket socket = new DatagramSocket()) {
			socketQueue.add(socket);	
			while(toContinue) {
			        byte[] buf = new byte[10];
			        buf = "pingUDP".getBytes();
			        InetAddress address = InetAddress.getLocalHost();
			        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 5000);
			        socket.send(packet);
			        
			        packet = new DatagramPacket(buf, buf.length);
			        socket.receive(packet);
			        System.out.println(new String(buf));
					Thread.sleep(waitTIme);
				}
				socket.close();
		} catch (Exception e) {
				if(toContinue) {
					System.out.println(e);					
				}
				toContinue = false;
		}
	}
	
	public void stoppingThread() {
		Runnable stopRunnable = new Runnable() {
			public void run() {
				try(BufferedReader input = new BufferedReader(new InputStreamReader(System.in))) {
					while(toContinue) {
						if(input.readLine().equals("exit")) {
							System.out.println("exit");
							toContinue = false;					
							for(DatagramSocket iterDatagramSocket : socketQueue) {
								iterDatagramSocket.close();
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
