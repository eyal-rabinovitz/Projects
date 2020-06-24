package il.co.ilrd.ws11;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class ex32 {

	public class Producer implements Runnable {
		LinkedList<Integer> linkedList;
		Integer i = 0;
		Semaphore semaphore;
		
	    public Producer(LinkedList<Integer> linkedList, Semaphore semaphore) {
	    	this.linkedList = linkedList;
	    	this.semaphore = semaphore;
	    }
	    
		@Override
		public void run() {
			while(true) {
				synchronized (linkedList) {
						linkedList.add(++i);
						System.out.println("producer");
						semaphore.release();
				}
			}
		}
	}
	
	public class Consumer implements Runnable{
		LinkedList<Integer> linkedList;

		Semaphore semaphore;
		
	    public Consumer(LinkedList<Integer> linkedList, Semaphore semaphore) {
	    	this.linkedList = linkedList;
	    	this.semaphore = semaphore;
	    }
	    
		@Override
		public void run() {
			while(true) {
				try {
					semaphore.acquire();
				synchronized(linkedList) {
						linkedList.remove(0);
						System.err.println("consumer");
				}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		
		LinkedList<Integer> linkedList = new LinkedList<Integer>();
		Semaphore semaphore = new Semaphore(0);

		Producer producer1 = new ex32().new Producer(linkedList, semaphore);
		Consumer consumer1 = new ex32().new Consumer(linkedList, semaphore);

		/*Producer producer2 = new ex32().new Producer(linkedList);
		Consumer consumer2 = new ex32().new Consumer(linkedList);
		
		Producer producer3 = new ex32().new Producer(linkedList);
		Consumer consumer3 = new ex32().new Consumer(linkedList);
		*/
		
		Thread tproducer = new Thread(producer1);
		Thread tconsumer = new Thread(consumer1);
		
		//while(true) {
			tproducer.start();
			//Thread.sleep(1);
			tconsumer.start();
			
			tproducer.join();
			tconsumer.join();
		//}
	}

}