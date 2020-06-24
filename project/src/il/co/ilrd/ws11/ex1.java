package il.co.ilrd.ws11;

public class ex1 {
	public static void main(String[] args) throws InterruptedException {
		System.out.println("main running");
		Thread thread = new Thread(new MyThread());
		thread.start();
		Thread.sleep(3);
		MyThread.stopThred();
		  
		Thread thread2 = new Thread(new MyRunnable());
		thread2.start();
		Thread.sleep(3);
		MyRunnable.stopThred();
		
		Boolean ch = true;
		System.out.println(ch? true : false);

	}
}
