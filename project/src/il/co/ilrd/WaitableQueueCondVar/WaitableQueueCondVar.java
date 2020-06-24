package il.co.ilrd.WaitableQueueCondVar;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class WaitableQueueCondVar <T> {
	private PriorityQueue<T> metaDataPQueue = null;
	private ReentrantLock lock = new ReentrantLock();
	private Condition conVar = lock.newCondition(); 
	
	public WaitableQueueCondVar() {
		this(null);
	}
	
	public WaitableQueueCondVar(Comparator<T> comp) {
		metaDataPQueue = new PriorityQueue<>(comp);
	}
	
	public void enqueue(T elem) {
		lock.lock();
		metaDataPQueue.add(elem);
		conVar.signalAll();
		lock.unlock();
	}
	
	public T dequeue() throws InterruptedException {
		lock.lock();
		while(metaDataPQueue.isEmpty()) {
			conVar.await();
		}
		
		T returnT = metaDataPQueue.poll();
		lock.unlock();
		
		return returnT;
	}

	public T dequeue(long timeout, TimeUnit unit) throws InterruptedException {
		lock.lock();
		while(metaDataPQueue.isEmpty()) {
			if(!conVar.await(timeout, unit)) {
				lock.unlock();
				
				return null;
			}
		}
		T returnT = metaDataPQueue.poll();
		lock.unlock();

		return returnT;
	}
	
	public boolean remove(T elem) throws InterruptedException {
		boolean isRemoved = false;

		if(lock.tryLock()) {
			isRemoved = metaDataPQueue.remove(elem);
			lock.unlock();
		}

		return isRemoved;
	}
	
	public class Enter implements Runnable {
		WaitableQueueCondVar<Integer> waitableQueue;
		
		public Enter(WaitableQueueCondVar<Integer> waitableQueue) {
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
		WaitableQueueCondVar<Integer> waitableQueue;
		
		public Remove(WaitableQueueCondVar<Integer> waitableQueue) {
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
			WaitableQueueCondVar<Integer> waitableQueue = new WaitableQueueCondVar<Integer>();
			//WaitableQueue<Integer> waitableQueue2 = new WaitableQueue<Integer>(new MyComperator());

			WaitableQueueCondVar<Integer>.Enter en = waitableQueue.new Enter(waitableQueue);
			
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