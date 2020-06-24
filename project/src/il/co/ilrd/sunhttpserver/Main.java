package il.co.ilrd.sunhttpserver;

public class Main {
	public static void main(String[] args) throws Exception {

		SunHttpServer sunServer = new SunHttpServer(8080);
		sunServer.start();
		Thread.sleep(100000);
	}
}
