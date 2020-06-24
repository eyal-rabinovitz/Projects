package il.co.ilrd.multithreaded.pingpongbroadcast2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

public class BroadcastTcpPingClient {
	private boolean isRunning = true;
	private long id = Thread.currentThread().getId();
	
	public void startClient() throws IOException, IOException {

		int portNumber = 55000;
		String hostName = "DESKTOP-TPI2AQK";
		
		try (Socket socket = new Socket(hostName, portNumber);
				PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
				BufferedReader bufferedReader =	new BufferedReader(new InputStreamReader(socket.getInputStream()));) {
					InputDetector exitDetector  = new InputDetector(() -> stopTcpClient(socket), "exit");
					exitDetector.start();
					while (isRunning) {
						printWriter.write("Ping! \n");
						printWriter.flush();
						System.out.println("Client received = " + bufferedReader.readLine());
					}
		}catch (SocketException e) {
			System.out.println("closing broadcast client");
		}
	}

	private void stopTcpClient(Socket socket) {
		isRunning = false;
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}