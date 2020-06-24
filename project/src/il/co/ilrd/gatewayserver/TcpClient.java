package il.co.ilrd.gatewayserver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonObject;
//JSON:
//{
//	"Commandkey": keyValue,
//	"Data": data (the data can be another json)
//}
public class TcpClient {

	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException, JSONException {
		ServerPort port = ServerPort.TCP_SERVER_PORT;

		String url = "localhost:3306/";
		String databaseName = "DatabaseManagementExample";
		String user = "root";
		String password = "132435";

		ByteBuffer buffer = ByteBuffer.allocate(4096); 
		SocketChannel client = SocketChannel.open(new InetSocketAddress(InetAddress.getLocalHost(), port.getPort()));
		
		JSONObject message = new JSONObject();
		JSONObject data = new JSONObject();
		data.put("dbName", "jarProject");
		data.put("sqlCommand", "CREATE TABLE Contact3 (id int, name int)");

		message.put("CommandKey" , "COMPANY_REGISTRATION");
		message.put("Data" , data);

		System.out.println("Client: sending message to server");
    	byte[] array1 = message.toString().getBytes("UTF-8");
		//System.out.println(message);

    	buffer.clear();
    	buffer = ByteBuffer.wrap(array1);
		client.write(buffer);
    	buffer.clear();

    	ByteBuffer returnbuffer = ByteBuffer.allocate(4096); 
		client.read(returnbuffer);
		buffer.flip();


		String returnmessage = new String(returnbuffer.array(), "UTF-8");
		System.out.println("returnmessage = " + returnmessage);

		Thread.sleep(1000);

		
		
		client.close();
	}
}


