package il.co.ilrd.pingpongtcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class server {

	public static void main(String[] args) throws IOException {
		int portNumber = 4444;
		
		System.out.println("Server: waiting for client");
		try(ServerSocket serverSocket = new ServerSocket(portNumber);
				Socket socket = serverSocket.accept();	    
				PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));){
			for(int i = 0; i < 5; ++i) {
		    	printWriter.write("Pong! \n");
		    	printWriter.flush();
		    	System.out.println("Server recieved: " + bufferedReader.readLine());
	        }
		}
	}
}
