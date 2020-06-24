package il.co.ilrd.ws11;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ex33 {

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_BLUE = "\u001B[34m";
	
	static int numOfConsumers = 3;
	static Lock lock = new ReentrantLock();
	Condition con  = lock.newCondition();
		
	public class Producer implements Runnable {
		Semaphore semaphore;
		
	    public Producer(Semaphore semaphore) {
	    	this.semaphore = semaphore;
	    }
	    
		@Override
		public void run() {
			while(true) {
				try {
					semaphore.acquire(numOfConsumers);
					System.out.println("          Producer ");
					lock.lock();
						con.signalAll();
					lock.unlock();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public class Consumer implements Runnable{
		Semaphore semaphore;
		
	    public Consumer(Semaphore semaphore) {
	    	this.semaphore = semaphore;
	    }
	    
		@Override
		public void run() {
			while(true) {
				lock.lock();
					semaphore.release();
				con.awaitUninterruptibly();
				lock.unlock();
				System.out.println("Consumer");
			}
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		
		Semaphore semaphore = new Semaphore(0);

		ex33 objEx33 = new ex33();
		Producer producer = objEx33.new Producer(semaphore);
		
		Consumer consumer1 = objEx33.new Consumer(semaphore);
		Consumer consumer2 = objEx33.new Consumer(semaphore);
		Consumer consumer3 = objEx33.new Consumer(semaphore);
		
		Thread tproducer = new Thread(producer);
		Thread tconsumer1 = new Thread(consumer1);
		Thread tconsumer2 = new Thread(consumer2);
		Thread tconsumer3 = new Thread(consumer3);

		//while(true) {
			tproducer.start();
			//Thread.sleep(1);
			tconsumer1.start();
			tconsumer2.start();
			tconsumer3.start();

			
			tproducer.join();
			tconsumer1.join();
			tconsumer2.join();
			tconsumer3.join();

		//}
	}
}