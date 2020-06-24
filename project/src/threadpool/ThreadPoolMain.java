package threadpool;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import threadpool.ThreadPool.TaskPriority;

public class ThreadPoolMain {

	public static void main(String[] args) throws Exception {
		//testShutdownException();
		//testPauseResume();
		//testPause();
		//testShutdown();
		testSetNumberOfThread();
		//testSetNumberOfThreadIncrease();
		//testCancel();
		//testAwaitTermination();
		//testExceptionInTask();
		
	}
	private static void testExceptionInTask() {
		ThreadPool tPool = new ThreadPool(10);
		Callable<String> task = new Callable<String>() {			
			@Override
			public String call() {	
				throw new ClassCastException("10");
			}
		};		
		
		Future<String> future = tPool.submitTask(task, TaskPriority.NORM);
		System.err.println();

		try {
			future.get();			
		} catch (InterruptedException | ExecutionException e) {		
			System.err.println(e.getCause() + "dd");
		}
	}
	private static void testAwaitTermination() throws Exception  {
		ThreadPool tPool = new ThreadPool(10);
		Callable<String> task = new Callable<String>() {			
			@Override
			public String call() throws Exception {	
				longTask(200000000l);
				return null;
			}
		};
		
		for (int i = 0; i < 3; i++) {
			tPool.submitTask(task, TaskPriority.NORM);
		}
		Thread.sleep(2000);

		tPool.shutdown();

		System.out.println(tPool.awaitTermination(50, TimeUnit.SECONDS));
		System.out.println("i am suppoesd to come after three threads finished");
		
	}
	private static void testShutdownException() throws Exception  {
		ThreadPool tPool = new ThreadPool(10);
		Callable<String> task = new Callable<String>() {			
			@Override
			public String call() throws Exception {	
				longTask(1000000000l);
				return null;
			}
		};
		
		for (int i = 0; i < 5; i++) {
			tPool.submitTask(task, TaskPriority.NORM);
		}
		tPool.shutdown();
		Thread.sleep(2000);
		System.out.println("shutdown - check that the app is not running after all printing");
		for (int i = 0; i < 5; i++) {
			tPool.submitTask(task, TaskPriority.NORM);
		}	
	}
	private static void testCancel() throws Exception {
		ThreadPool tPool = new ThreadPool(1);
		Callable<String> task = new Callable<String>() {			
			@Override
			public String call() throws Exception {	
				longTask(1000000000l);
				return null;
			}
		};
		
		for (int i = 0; i < 2; i++) {
			tPool.submitTask(task, TaskPriority.NORM);
		}
		Future<String> future = tPool.submitTask(() -> {
			System.out.println("i am supposed to be canceld!");
			return "return test";
		});
		future.cancel(true);
		Thread.sleep(1000);
		System.out.println("Only two threads should appear");
		
	}
	private static void testShutdown() throws Exception {
		ThreadPool tPool = new ThreadPool(10);
		Callable<String> task = new Callable<String>() {			
			@Override
			public String call() throws Exception {	
				longTask(1000000000l);
				return null;
			}
		};
		
		for (int i = 0; i < 10; i++) {
			tPool.submitTask(task, TaskPriority.NORM);
		}
		Thread.sleep(2000);
		tPool.shutdown();
		System.out.println("shutdown - check that the app is not running after all printing");			
	}
	protected static void longTask(long timeLimit) {
		for (int i = 0; i <= timeLimit; i++) {
			if (i == timeLimit) {
				System.out.println("finished " + Thread.currentThread().getName());
			}
		}		
	}
	private static void testPauseResume() throws Exception {
		ThreadPool tPool = new ThreadPool(10);
		Callable<String> task = new Callable<String>() {			
			@Override
			public String call() throws Exception {	
				final long limit = 1000;
				for (int i = 0; i <= limit; i++) {
					if (i == limit) {
						System.out.println("finished " + Thread.currentThread().getName());
					}
				}
				return null;
			}
		};
		
		for (int i = 0; i < 10; i++) {
			tPool.submitTask(task, TaskPriority.NORM);
		}
		tPool.pause();
		Thread.sleep(2000);
		tPool.resume();
		System.out.println("paused and then resume - more printing after this");
		for (int i = 0; i < 10; i++) {
			tPool.submitTask(task, TaskPriority.NORM);
		}		
	}

	private static void testPause() throws Exception {
		ThreadPool tPool = new ThreadPool(10);
		Callable<String> task = new Callable<String>() {
			
			@Override
			public String call() throws Exception {	
				final long limit = 1000;
				for (int i = 0; i <= limit; i++) {
					if (i == limit) {
						System.out.println("finished " + Thread.currentThread().getName());
					}
				}
				return null;
			}
		};
		
		for (int i = 0; i < 10; i++) {
			tPool.submitTask(task, TaskPriority.NORM);
		}
		tPool.pause();
		Thread.sleep(2000);

		System.out.println("paused - no more printing after this");
		for (int i = 0; i < 10; i++) {
			tPool.submitTask(task, TaskPriority.NORM);
		}
		
	}

	static void testSetNumberOfThreadIncrease() throws Exception {
		System.out.println("*********** one Thread doing all the job *************");

		ThreadPool tPool = new ThreadPool(1);
		Runnable task = new Runnable() {
			@Override
			public void run() {
					longTask(1000000000l);
			}
		};
		for (int i = 0; i < 10; i++) {
			tPool.submitTask(task, TaskPriority.NORM);
		}
		Thread.sleep(2000);
		System.out.println("*********** Five Threads should be here *************");
		tPool.setNumberOfThread(5);
		for (int i = 0; i < 10; i++) {
			tPool.submitTask(task, TaskPriority.NORM);
		}
	}

	static void testSetNumberOfThread() throws InterruptedException {
		ThreadPool tPool = new ThreadPool(5);
		Callable<String> task = new Callable<String>() {

			@Override
			public String call() throws Exception {
				System.out.println("task1:" + Thread.currentThread().getName());
				return null;
			}
		};
		Callable<String> task2 = new Callable<String>() {

			@Override
			public String call() throws Exception {
				longTask(1000000);
				return null;
			}
		};
		for (int i = 0; i < 10; i++) {
			tPool.submitTask(task, TaskPriority.NORM);
		}
		tPool.setNumberOfThread(0);

		Thread.sleep(2000);
		System.out.println("*********num of thread is zero, so calling submit does nothing");

		for (int i = 0; i < 10; i++) {
			tPool.submitTask(task2, TaskPriority.NORM);
		}

		Thread.sleep(2000);
		System.out.println("*********num of thread is now two, so two threads should be here");
		tPool.setNumberOfThread(2);

		for (int i = 0; i < 5; i++) {
			tPool.submitTask(task2, TaskPriority.NORM);
		}
	}

}