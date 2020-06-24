package il.co.ilrd.ws11;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class ex3 {
	
	AtomicInteger varAtomicInteger = new AtomicInteger();

	ReentrantLock lock = new ReentrantLock();

	public class Ping implements Runnable{
	    Semaphore semping;
	    Semaphore sempong;

	    public Ping(Semaphore semping, Semaphore sempong) {
	    	this.semping = semping;
	    	this.sempong = sempong;
	    }
	    
		@Override
		public void run() {
			while(true) {
				try {
					sempong.acquire();
						System.err.println("ping");
					semping.release();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
				
				/*
				if(varAtomicInteger.get() == 0) {
					System.out.println("ping");
					varAtomicInteger.set(1);

				}*/
			}
		}
	}
	
	public class Pong implements Runnable{
	    Semaphore sempong;
	    Semaphore semping;

	    public Pong(Semaphore semping, Semaphore sempong) {
	    	this.semping = semping;
	    	this.sempong = sempong;
	    }
	    
		@Override
		public void run() {
			while(true) {
				try {
					semping.acquire();
						System.err.println("pong");
					sempong.release();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
/*
				if(varAtomicInteger.get() == 1) {
					System.out.println("pong");
					varAtomicInteger.set(0);;
				}*/
			}
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		
		Semaphore semping = new Semaphore(0);
		Semaphore sempong = new Semaphore(1);

		Ping ping = new ex3().new Ping(semping, sempong);
		Pong pong = new ex3().new Pong(semping, sempong);
		
		Thread tping = new Thread(ping);
		Thread tpong = new Thread(pong);
		
		tping.start();
		//Thread.sleep(1);
		tpong.start();


	}

}
