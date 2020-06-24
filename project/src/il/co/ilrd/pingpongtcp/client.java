package il.co.ilrd.pingpongtcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class client {
	public static void main(String[] args) throws UnknownHostException, IOException {
		int portNumber = 4444;
		String hostName = "DESKTOP-TPI2AQK";

		System.out.println("Client: sending message to server");
		try(Socket socket = new Socket(hostName, portNumber);		
				PrintWriter printWriter =
						new PrintWriter(socket.getOutputStream());
				BufferedReader bufferedReader =
						new BufferedReader(new InputStreamReader(socket.getInputStream()));) {
			for(int i = 0; i < 5; ++i) {
	        	System.out.println(bufferedReader.readLine());
	        	printWriter.write("Ping! \n");
	        	printWriter.flush();
			}
		}     
	}
}