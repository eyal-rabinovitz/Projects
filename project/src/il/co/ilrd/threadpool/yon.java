//package tichange;
////package il.co.ilrd.threadpool;
////
////package il.co.ilrd.thread_pool;
////
////import java.util.ArrayList;
////import java.util.List;
////import java.util.concurrent.Callable;
////import java.util.concurrent.ExecutionException;
////import java.util.concurrent.Executor;
////import java.util.concurrent.Executors;
////import java.util.concurrent.Future;
////import java.util.concurrent.Semaphore;
////import java.util.concurrent.TimeUnit;
////import java.util.concurrent.TimeoutException;
////
////import il.co.ilrd.waitable_queue.WaitableQueueSem;
////
////public class ThreadPool implements Executor {	
////	/*not sure about the list type (maybe list of Thread)*/
////	private List<TPThread<?>> threadsList = new ArrayList<>();
////	private WaitableQueueSem<ThreadPoolTask<?>> tasksQueue = new WaitableQueueSem<>();;
////	private final static int DEAFULT_NUM_THREADS = Runtime.getRuntime().availableProcessors();
////	private int numOfThreads;
////	private final static int VIP_PRIORITY = 100;
////
////	
////	public enum TaskPriority {
////		MIN,
////		NORM,
////		MAX
////	}
////	
////	public ThreadPool() {
////		this(DEAFULT_NUM_THREADS);
////	}
////	
////	
////	private void AddAndStartThread() {
////		TPThread<?> newThread = new TPThread<>();
////		threadsList.add(newThread);
////		newThread.start();	
////	}
////	
////	public <T> ThreadPool(int num) {
////		numOfThreads = num;
////		
////		for (; num > 0; --num) {
////			AddAndStartThread();
////			System.out.println("new thread " + num); //debug
////		}
////		
//////		for (; num > 0; --num) {
//////			threadsList.add(new TPThread<T>());
//////			System.out.println("new thread " + num); //debug
//////		}
//////		
//////		for(TPThread<?> thread: threadsList) {
//////			thread.start();
//////		}
////	}
////	
////	private class TPThread<T> extends Thread {
////		private boolean toRun = true;
////
////		@Override
////		public void run() {
////			while(toRun) {
////				try {
////					tasksQueue.dequeue().runTask();
////					
////				} catch (Exception e) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				}
////				
////			}
////			System.out.println("running thread "); //debug
////		}
////	}
////	
////	private <T> Future<T> submitTaskGeneric(Callable<T> callable, int taskPriority) {
////		ThreadPoolTask<T> newTask = new ThreadPoolTask<T>(taskPriority, callable);
////		tasksQueue.enqueue(newTask);
////		
////		return newTask.getFuture();
////	}
////	
////	public <T> Future<T> submitTask(Callable<T> callable) {
////		return submitTaskGeneric(callable, TaskPriority.NORM.ordinal());
////	}
////	
////	public <T> Future<T> submitTask(Callable<T> callable, TaskPriority taskPriority) {
////		return submitTaskGeneric(callable, taskPriority.ordinal());
////	}
////	
////	public Future<Void> submitTask(Runnable runnable, TaskPriority taskPriority) {
////		Callable<Void> callableObj = Executors.callable(runnable, null);
////		
////		return submitTaskGeneric(callableObj, taskPriority.ordinal());
////	}
////	
////	public <T> Future<T> submitTask(Runnable runnable, TaskPriority taskPriority, T result) {
////		Callable<T> callableObj = Executors.callable(runnable, result);
////		
////		return submitTaskGeneric(callableObj, taskPriority.ordinal());
////	}
////	
////	
////	public void setNumberOfThread(int updatedThreadsNum) {
////		if(updatedThreadsNum > this.numOfThreads) {
////			for(int i = 0; i < updatedThreadsNum - this.numOfThreads; ++i) {
////				AddAndStartThread();
////			}
////		} else {
////			class stopTask<T> implements Callable<T> {
////				@Override
////				public T call() throws Exception {
////					TPThread<?> currentThread = (TPThread<?>)Thread.currentThread();
////					currentThread.toRun = false;
////					System.err.println("stopTaskStopped task");
////					return null;
////				}
////			}
////			
////			for(int i = 0; i < this.numOfThreads - updatedThreadsNum; ++i) {	
////				this.submitTaskGeneric(new stopTask<Void>(), VIP_PRIORITY);
////			}
////		}
////	}
////	
////	@Override
////	public void execute(Runnable runnable) {//exceptions?
////		submitTask(runnable, TaskPriority.NORM);
////	}
////	
////	public void pause() {
////		
////	}
////	
////	public void resume() {
////		
////	}
////	
////	public void shutdown() {
////		
////	}
////
////	public void awaitTermination() {
////		
////	}
////	
////	private class ThreadPoolTask<T> implements Comparable<ThreadPoolTask<T>> {	
////		private int taskPriority;
////		private Callable<T> callable;
////		/*private*/ TaskFuture taskFuture = new TaskFuture();
////		private Semaphore runTaskSem = new Semaphore(0);
////
////		
////		
////		public ThreadPoolTask(int taskPriority, Callable<T> callable) {
////			
////			
////			this.taskPriority = taskPriority;
////			this.callable = callable;
////		}
////		
////		public TaskFuture getFuture() {
////			return taskFuture;
////		}
////
////		@Override
////		public int compareTo(ThreadPoolTask<T> other) {
////			return other.taskPriority - this.taskPriority;
////		}
////		
////		private void runTask() throws Exception {
////			taskFuture.returnObj = callable.call();
////			taskFuture.isDone = true;
////			runTaskSem.release();
////		}
////		
////		private class TaskFuture implements Future<T> {
////			private boolean isDone = false;
////			T returnObj;
////			
////			@Override
////			public boolean cancel(boolean arg0) {
////				// TODO Auto-generated method stub
////				return false;
////			}
////
////			@Override
////			public T get() throws InterruptedException, ExecutionException {
////				runTaskSem.acquire();
////				
////				return returnObj;
////			}
////
////			@Override
////			public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
////				if(runTaskSem.tryAcquire(timeout,unit)) {
////					return returnObj;
////					
////				} else {
////					throw new TimeoutException();
////				}
////			}
////
////			@Override
////			public boolean isCancelled() {
////				// TODO Auto-generated method stub
////				return false;
////			}
////
////			@Override
////			public boolean isDone() {
////				return isDone;
////			}
////			
////		}
////	}	
////}
//
//
//
//
//
//package il.co.ilrd.threadpool;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.Callable;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.Executor;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
//import java.util.concurrent.Semaphore;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.TimeoutException;
//
//import il.co.ilrd.waitable_queue.WaitableQueueSem;
//
//public class ThreadPool implements Executor {	
//	private List<TPThread> threadsList = new ArrayList<>();
//	private int numOfThreads;
//	private boolean isShutdown = false;
//	private Semaphore pauseSem = new Semaphore(0);
//	private Semaphore awaitSem = new Semaphore(0);
//	private WaitableQueueSem<ThreadPoolTask<?>> tasksQueue = 
//													new WaitableQueueSem<>();
//	private final static int DEAFULT_NUM_THREADS = 
//									Runtime.getRuntime().availableProcessors();
//	
//	private final static int MAX_VIP_PRIORITY = 10;
//	private final static int MIN_VIP_PRIORITY = -10;
//	
//	public enum TaskPriority {
//		MIN,
//		NORM,
//		MAX
//	}
//	
//	public ThreadPool() {
//		this(DEAFULT_NUM_THREADS);
//	}
//	
//	public <T> ThreadPool(int numOfThreads) {
//		this.numOfThreads = numOfThreads;
//		createAndStartThread(numOfThreads);
//	}
//	
//	private void createAndStartThread(int numOfThreads) {
//		for (int i = 0; i < numOfThreads; i++) {
//			threadsList.add(new TPThread());
//		}
//		
//		for (TPThread tpThread : threadsList) {
//			tpThread.start();
//		}
//	}
//	
//	/*---------------------------- Class TPThread ----------------------------*/
//	
//	private class TPThread extends Thread {
//		ThreadPoolTask<?> curTask = null;
//		private boolean toRun = true;
//		
//		@Override
//		public void run() {
//			while(toRun) {
//				try {
//					curTask = tasksQueue.dequeue();
//					if(!curTask.getFuture().isCancelled()) {
//						curTask.runTask();
//					}
//				} catch (Exception e) {
//					curTask.getFuture().taskException = e;
//					curTask.runTaskSem.release();
//				}
//				
//			}
//			
//			threadsList.remove(this);
//			
//			if(threadsList.isEmpty()) {
//				awaitSem.release();
//			}
//		}
//	}
//	
//	/*---------------------------- API functions -----------------------------*/
//
//	private <T> Future<T> submitTask(Callable<T> callable, int taskPriority) {
//		if(true == isShutdown) {
//			throw new SubmitAfterShutdownException();
//		}
//		
//		ThreadPoolTask<T> task = new ThreadPoolTask<>(callable, taskPriority);
//		tasksQueue.enqueue(task);
//		
//		return task.getFuture();
//	}
//	
//	public <T> Future<T> submitTask(Callable<T> callable) {
//		return submitTask(callable, TaskPriority.NORM.ordinal());
//	}
//	
//	public <T> Future<T> submitTask(Callable<T> callable, 
//											TaskPriority taskPriority) {
//		return submitTask(callable, taskPriority.ordinal());
//	}
//	
//	public Future<Void> submitTask(Runnable runnable, 
//											TaskPriority taskPriority) {
//		return submitTask(Executors.callable(runnable, null), 
//													taskPriority.ordinal());
//	}
//	
//	public <T> Future<T> submitTask(Runnable runnable, 
//										TaskPriority taskPriority, T t) {
//		return submitTask(Executors.callable(runnable,t), 
//												taskPriority.ordinal());
//	}
//	
//	public void setNumberOfThread(int numOfThreads) {
//		if(this.numOfThreads < numOfThreads) {
//			createAndStartThread(numOfThreads - this.numOfThreads);
//		}
//		else {
//			int numOfTasksToAdd = this.numOfThreads - numOfThreads;
//			addNumOfTasks(new StopTask<>(), numOfTasksToAdd, MAX_VIP_PRIORITY);
//		}
//		
//		this.numOfThreads = numOfThreads;
//	}
//	
//	@Override
//	public void execute(Runnable runnable) {
//		submitTask(runnable, TaskPriority.NORM);
//	}
//	
//	public void pause() {
//		addNumOfTasks(new PauseTask<>(), numOfThreads, MAX_VIP_PRIORITY);
//	}
//	
//	public void resume() {
//		pauseSem.release(numOfThreads);
//	}
//	
//	public void shutdown() {
//		addNumOfTasks(new StopTask<>(), numOfThreads, MIN_VIP_PRIORITY);
//		
//		numOfThreads = 0;
//		isShutdown = true;
//	}
//
//	public boolean awaitTermination(long timeout, TimeUnit unit) 
//												throws InterruptedException {
//		return awaitSem.tryAcquire(timeout, unit);
//	}
//	
//	private <T> void addNumOfTasks(Callable<T> task, int numOfTasks, 
//														int priority) {
//		for(int numOfSubmit = 0; numOfSubmit < numOfTasks; ++numOfSubmit) {
//			submitTask(task, priority);
//		}
//	}
//	
//	/*------------------------- Class ThreadPoolTask -------------------------*/
//
//	private class ThreadPoolTask<T> implements Comparable<ThreadPoolTask<T>> {	
//		private int taskPriority;
//		private Callable<T> callable;
//		private TaskFuture taskFuture = new TaskFuture();
//		private Semaphore runTaskSem = new Semaphore(0);
//		
//		public ThreadPoolTask(Callable<T> callable, int taskPriority) {
//			this.callable = callable;
//			this.taskPriority = taskPriority;
//		}
//		
//		public TaskFuture getFuture() {
//			return taskFuture;
//		}
//
//		@Override
//		public int compareTo(ThreadPoolTask<T> otherTask) {
//			return otherTask.taskPriority - taskPriority;
//		}
//		
//		private void runTask() throws Exception {
//			taskFuture.returnObj = callable.call();
//			taskFuture.isDone = true;
//			runTaskSem.release();
//		}
//		
//		/*------------------------- Class TaskFuture -------------------------*/
//		
//		private class TaskFuture implements Future<T> {
//			private boolean isDone = false;
//			private boolean isCancelled = false;
//			private T returnObj = null;
//			private Throwable taskException = null;
//			
//			@Override
//			public boolean cancel(boolean arg0) {
//				try {
//					isCancelled = tasksQueue.remove(ThreadPoolTask.this);
//				} catch (InterruptedException e) {
//					taskException = e;
//					runTaskSem.release();
//				}
//
//				return isCancelled;
//			}
//
//			@Override
//			public T get() throws InterruptedException, ExecutionException {
//				runTaskSem.acquire();
//				
//				checkException();
//				
//				return returnObj;
//			}
//
//			@Override
//			public T get(long timeout, TimeUnit unit) 
//						throws InterruptedException, ExecutionException, 
//													TimeoutException {
//				runTaskSem.tryAcquire(timeout, unit);
//				
//				checkException();
//				
//				return returnObj;
//			}
//
//			@Override
//			public boolean isCancelled() {
//				return isCancelled;
//			}
//
//			@Override
//			public boolean isDone() {
//				return isDone;
//			}
//			
//			private void checkException() throws ExecutionException {
//				if(null != taskException) {
//					throw new ExecutionException(taskException);
//				}
//			}
//			
//		}
//	}
//	
//	/*------------------------- StopTask & PauseTask -------------------------*/
//	
//	private class StopTask<T> implements Callable<T> {
//		@Override
//		public T call() throws Exception {
//			TPThread currThread = (TPThread) Thread.currentThread();
//			currThread.toRun = false;
//			return null;
//		}
//		
//	}
//	
//	private class PauseTask<T> implements Callable<T> {
//
//		@Override
//		public T call() throws Exception {
//			pauseSem.acquire();
//	
//			return null;
//		}
//
//	}
//	
//	/*------------------------- Class Exception -------------------------*/
//
//	private class SubmitAfterShutdownException extends RuntimeException {
//		private static final long serialVersionUID = 1L;
//		
//	}
//}