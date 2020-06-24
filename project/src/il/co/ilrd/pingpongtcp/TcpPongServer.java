package il.co.ilrd.pingpongtcp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class TcpPongServer {

	static boolean toRun = true;
	
    public static void main(String[] args) throws Exception {
    	Thread ping = new Ping();
    	Thread pong = new Pong();

    	pong.start();
    	ping.start();

    	Thread.sleep(10000);
    	toRun = false;
    }

    private static class Ping extends Thread {

        @Override
        public void run() {
        	try (
        		    Socket client = new Socket("DESKTOP-TPI2AQK", 666);
        		    PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
        		    BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        		)
        		{ while(toRun) {
                	System.out.println(reader.readLine());
                	Thread.sleep(1000);
                	writer.write("ping \n");
                	writer.flush();
                }
            } catch (Exception e) {
            }
        }
    }
    
    private static class Pong extends Thread {
    	
        @Override
        public void run() {
        	try ( 
        		    ServerSocket serverSocket = new ServerSocket(666);
        		    Socket clientSocket = serverSocket.accept();
        		    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        		    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        		)
        		{   while(toRun) {
            	 	out.write("pong \n");
            	 	out.flush();
                	System.out.println(in.readLine());
                	Thread.sleep(1000);
                }
            } catch (Exception e) {
            }
        }
    }
}