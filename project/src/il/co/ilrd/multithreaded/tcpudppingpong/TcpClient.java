package il.co.ilrd.multithreaded.tcpudppingpong;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class TcpClient {
	public static void main(String[] args) throws UnknownHostException, IOException {
		int portNumber = 4444;
		String hostName = "DESKTOP-TPI2AQK";

		System.out.println("Client: sending message to server");
		try(Socket socket = new Socket(hostName, portNumber);		
				PrintWriter printWriter =
						new PrintWriter(socket.getOutputStream());
				BufferedReader bufferedReader =
						new BufferedReader(new InputStreamReader(socket.getInputStream()));) {
		        	printWriter.write("Ping! \n");
		        	printWriter.flush();
		        	System.out.println("Client received = " + bufferedReader.readLine());
			
		}     
	}
}
