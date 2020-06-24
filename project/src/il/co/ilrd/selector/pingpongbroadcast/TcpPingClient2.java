package il.co.ilrd.selector.pingpongbroadcast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TcpPingClient2 {
	public static void main(String[] args) throws IOException, InterruptedException {
		//boolean toContinue = true;
		int portNumber = 50000;
		String hostName = "DESKTOP-TPI2AQK";

		System.out.println("Client: sending message to server");
		try(Socket socket = new Socket(hostName, portNumber);		
				PrintWriter printWriter =
						new PrintWriter(socket.getOutputStream());
				BufferedReader bufferedReader =
						new BufferedReader(new InputStreamReader(socket.getInputStream()));) {
			for(int i = 0; i < 6; ++i) {
		        	printWriter.write("TCP2 Ping!222222222 \n");
		        	printWriter.flush();
		        	System.out.println("Client received = " + bufferedReader.readLine());
		        	Thread.sleep(500);
			}
		}     
	}
}