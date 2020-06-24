package il.co.ilrd.multithreaded.pingpongbroadcast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.LinkedBlockingDeque;


public class BroadcastUdpPingClient {
	private LinkedBlockingDeque<DatagramSocket> socketQueue= new LinkedBlockingDeque<>();
	private boolean toContinue = true;
	private int waitTIme = 5000;
	
	public static void main(String[] args) {
		BroadcastUdpPingClient udpBroadcastClient = new BroadcastUdpPingClient();
		udpBroadcastClient.runUDPBroadcastClien();
		udpBroadcastClient.stoppingThread();
	}

	private void runUDPBroadcastClien() {
		Runnable clientRunnable = new Runnable() {
			public void run() {
				UDPBroadcastClientMethod();	
			}

		};
		for(int i = 0; i < 1; ++i)
			new Thread(clientRunnable).start();		
	}

	private void UDPBroadcastClientMethod() {
		byte[] buf = new byte[20];
		try(DatagramSocket socket = new DatagramSocket()) {
			socketQueue.add(socket);
			while(toContinue) {				
				buf = "pingBC".getBytes();
				DatagramPacket packet = new DatagramPacket(buf, buf.length, 
				InetAddress.getByName("255.255.255.255"), 5001);
				socket.send(packet);
				packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				System.out.println(new String(buf));
		        InetAddress address = packet.getAddress();
		        int port = packet.getPort();
	        	packet = new DatagramPacket(buf, buf.length, address, port);
	        	Thread.sleep(waitTIme);
	        	socket.send(packet);	      
			}
		} catch(Exception e) {
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
