package il.co.ilrd.ws11;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class ex2 {

	public class LockThread implements Runnable {
		ReentrantLock lock1 = new ReentrantLock();

		@Override
		public /*synchronized*/ void run() {
			for (int i = 0; i < 10000000; ++i) {
				//lock1.lock();
				//synchronized(this) {
				counterAtomicInteger.incrementAndGet();
				//++counter;
				//}
				//lock1.unlock();
			}			
		}
	}
	
	//private static int counter = 0;
	private static AtomicInteger counterAtomicInteger = new AtomicInteger();
	
	public static void main(String[] args) throws InterruptedException {

		System.out.println("main running");
		LockThread obj = new ex2().new LockThread();
		Thread thread1 = new Thread(obj);
		Thread thread2 = new Thread(obj);

		/*
		Thread thread2 = new Thread(new Thread() {
			@Override
		    public void run() {
				for (int i = 0; i < 10000000; ++i) {
					++counter;
				}
			}
		});
		*/
		long startTime = System.nanoTime();
		
		thread1.run();
		thread2.run();
		thread1.join();
		thread2.join();
		System.out.println(counterAtomicInteger);

		long endTime = System.nanoTime();
		long timeElapsed = endTime - startTime;
		
		System.out.println("Execution time in milliseconds  : " + timeElapsed/ 1000000);
	}
}
