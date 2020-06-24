package il.co.ilrd.gatewayserver;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
//JSON:
//{
//	"Commandkey": keyValue,
//	"Data": data (the data can be another json)
//}
public class Main {
	public static void main(String[] args) {
		GatewayServer server = null;
		try {
			server = new GatewayServer("FactoryCommandModifier");
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException | InstantiationException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			server.addLowHttpServer(ServerPort.HTTP_SERVER_PORT);
			//server.addHighHttpServer(ServerPort.HTTP_SERVER_PORT);
			server.addTcpServer(ServerPort.TCP_SERVER_PORT);
			server.addUdpServer(ServerPort.UDP_SERVER_PORT);
			//server.addUdpServer(ServerPort.UDP_SERVER_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		server.start();
		
		
		
	}
}
