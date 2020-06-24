package il.co.ilrd.WaitableQueueSem;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class WaitableQueueSem <T> {
	private PriorityQueue<T> metaDataPQueue = null;
	private Semaphore numOfElementsSemaphore = new Semaphore(0);
	private ReentrantLock lock = new ReentrantLock();
	
	public WaitableQueueSem() {
		this(null);
	}
	
	public WaitableQueueSem(Comparator<T> comp) {
		metaDataPQueue = new PriorityQueue<>(comp);
	}
	
	public void enqueue(T elem) {
		lock.lock();
		metaDataPQueue.add(elem);
		lock.unlock();
		numOfElementsSemaphore.release();
	}
	
	public T dequeue() throws InterruptedException {
		numOfElementsSemaphore.acquire();
		lock.lock();
		T returnT = metaDataPQueue.poll();
		lock.unlock();
		
		return returnT;
	}

	public T dequeue(long timeout, TimeUnit unit) throws InterruptedException {
		T returnT = null;
		
		if(numOfElementsSemaphore.tryAcquire(timeout, unit)) {
			lock.lock();
			returnT = metaDataPQueue.poll();
			lock.unlock();
		}
		
		return returnT;
	}
	
	public boolean remove(T elem) throws InterruptedException {
		boolean isRemoved = false;

		if(numOfElementsSemaphore.tryAcquire()) {
			if(lock.tryLock()) {
				isRemoved = metaDataPQueue.remove(elem);
				if(!isRemoved) {
					numOfElementsSemaphore.release();
				}
				lock.unlock();
			}
			else {
				numOfElementsSemaphore.release();
			}
		}

		return isRemoved;
	}
	
	public class Enter implements Runnable {
		WaitableQueueSem<Integer> waitableQueue;
		
		public Enter(WaitableQueueSem<Integer> waitableQueue) {
			this.waitableQueue = waitableQueue;
		}
		
		volatile int int1 = 10;
		
		@Override
		public void run() {			
			while(true) {
				waitableQueue.enqueue(++int1);

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}			
		}
	}
	
	public class Remove implements Runnable {
		WaitableQueueSem<Integer> waitableQueue;
		
		public Remove(WaitableQueueSem<Integer> waitableQueue) {
			this.waitableQueue = waitableQueue;
		}

		@Override
		public void run() {
			while(true) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}			
		}
	}
	
	public static class MyComperator implements Comparator<Integer> {
		@Override
		public int compare(Integer arg0, Integer arg1) {
			return arg0 - arg1;
		}	
	}
	
	public static void main(String[] args) throws InterruptedException {
		WaitableQueueSem<Integer> waitableQueue = new WaitableQueueSem<Integer>();
		//WaitableQueue<Integer> waitableQueue2 = new WaitableQueue<Integer>(new MyComperator());

		WaitableQueueSem<Integer>.Enter en = waitableQueue.new Enter(waitableQueue);
		
		Thread enterThread1 = new Thread(en);
		//Thread enterThread2 = new Thread(en);
		//Thread enterThread3 = new Thread(en);

		Thread removeThread = new Thread(waitableQueue.new Remove(waitableQueue));
		
		enterThread1.start();
		//enterThread2.start();
		//enterThread3.start();

		removeThread.start();

		
		
		
		//waitableQueue.enqueue(10);
		//waitableQueue.enqueue(11);
		//waitableQueue.enqueue(12);

		//System.out.println(waitableQueue.dequeue());
		//System.out.println(waitableQueue.dequeue(3, TimeUnit.SECONDS));
/*		System.out.println(waitableQueue.dequeue());
		 */
	}
}