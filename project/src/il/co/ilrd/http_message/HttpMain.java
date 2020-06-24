package il.co.ilrd.http_message;

public class HttpMain {

	public static void main(String[] args) {
		HttpServer server = new HttpServer();
		new Thread(server).start();
	}

}
