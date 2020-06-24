package il.co.ilrd.threadpool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import il.co.ilrd.WaitableQueueSem.WaitableQueueSem;

public class ThreadPool implements Executor {	
	
	private int numOfThreads;
	private List<TPThread<?>> threadsList = new ArrayList<>();
	private WaitableQueueSem<ThreadPoolTask<?>> tasksQueue = new WaitableQueueSem<>();;
	private Semaphore pauseSemaphore = new Semaphore(0);
	private Semaphore awaitSemaphore = new Semaphore(0);
	private final static int HIGHEST_PRIORITY = TaskPriority.PRIORITY_NUM.ordinal() + 1;
	private final static int LOWEST_PRIORITY = TaskPriority.MIN.ordinal() - 1;
	private final static int DEAFULT_NUM_THREADS = Runtime.getRuntime().availableProcessors();
	
	public enum TaskPriority {
		MIN,
		NORM,
		MAX,
		PRIORITY_NUM
	}

	public ThreadPool() {
		this(DEAFULT_NUM_THREADS);
	}
	
	public ThreadPool(int numOfThreads) {
		this.numOfThreads = numOfThreads;

		for(int i = 0; i < numOfThreads; ++i) {
			AddAndStartThread();
		}
	}
	
	private <T> Future<T> submitTaskGeneric(Callable<T> callable, int taskPriority) {
		ThreadPoolTask<T> poolTask = new ThreadPoolTask<T>(callable, taskPriority);
		tasksQueue.enqueue(poolTask);
		
		return poolTask.getFuture();
	}
	
	public <T> Future<T> submitTask(Callable<T> callable) {
		return submitTaskGeneric(callable, TaskPriority.NORM.ordinal());
	}
	
	public <T> Future<T> submitTask(Callable<T> callable, TaskPriority taskPriority) {
		return submitTaskGeneric(callable, taskPriority.ordinal());
	}
	
	public Future<Void> submitTask(Runnable runnable, TaskPriority taskPriority) {
		Callable<Void> callable = Executors.callable(runnable, null);
		
		return submitTaskGeneric(callable, taskPriority.ordinal());
	}
	
	public <T> Future<T> submitTask(Runnable runnable, TaskPriority taskPriority, T t) {
		Callable<T> callable = Executors.callable(runnable, t);
		
		return submitTaskGeneric(callable, taskPriority.ordinal());
	}
	
	public void setNumberOfThread(int newNumOfThreads) {
		if(newNumOfThreads > numOfThreads) {
			for(int i = 0; i <  newNumOfThreads - numOfThreads; ++i) {
				AddAndStartThread();
			}
			numOfThreads += newNumOfThreads;
		}
		else {
			for(int i = 0; i <  numOfThreads - newNumOfThreads; ++i) {
				this.submitTaskGeneric(new stopTask<>(), HIGHEST_PRIORITY);
			}
		}
	}
	
	private void AddAndStartThread() {
		TPThread<?> newThread = new TPThread<>();
		threadsList.add(newThread);
		newThread.start();
	}
		
	@Override
	public void execute(Runnable runnable) {
		submitTask(runnable, TaskPriority.NORM);
	}
	
	public <T> void pause() {
		class pauseTask implements Callable<T> {
			@Override
			public T call() throws Exception {
				pauseSemaphore.acquire();
				
				return null;
			}
		}
		
		for(int i = 0; i <  numOfThreads; ++i) {
			this.submitTaskGeneric(new pauseTask(), HIGHEST_PRIORITY);
		}
	}
	
	public void resume() {
		pauseSemaphore.release(numOfThreads);
	}
	
	public void shutdown() {
		for(int i = 0; i <  numOfThreads; ++i) {
			this.submitTaskGeneric(new stopTask<>(), LOWEST_PRIORITY);
		}
	}

	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		return awaitSemaphore.tryAcquire(timeout, unit);
	}
	
/*************************************** TPThread **************************************************/
	private class TPThread<T> extends Thread {
		
		private boolean toRun = true;
		private	ThreadPoolTask<?> currTask = null;

		@Override
		public void run() {
			while(toRun) {
				try {
					currTask = tasksQueue.dequeue();
					if(!currTask.getFuture().isCancelled()) {
						currTask.runTask();						
					}
				} catch (Exception e) {

				}
			}
			threadsList.remove(this);
			--numOfThreads;
			
			if(threadsList.isEmpty()) {
				awaitSemaphore.release();
			}
		}
	}
	
/*************************************** stopTask **************************************************/
	private class stopTask<T> implements Callable<T> {
		
		@Override
		public T call() throws Exception {
			Thread thread = Thread.currentThread();
			TPThread<?> currentThread = (TPThread<?>)thread;
			currentThread.toRun = false;
			
			return null;
		}
	}

/****************************************** ThreadPoolTask ***********************************************/
	private class ThreadPoolTask<T> implements Comparable<ThreadPoolTask<T>> {	
		
		private int taskPriority;
		private Callable<T> callable;
		private TaskFuture taskFuture = new TaskFuture();
		private Semaphore runTaskSem = new Semaphore(0);
		private boolean isCancelled = false;

		public ThreadPoolTask(Callable<T> callable, int taskPriority) {
			this.taskPriority = taskPriority;
			this.callable = callable;
		}
	
		public TaskFuture getFuture() {
			return taskFuture;
		}

		@Override
		public int compareTo(ThreadPoolTask<T> other) {
			return other.taskPriority - this.taskPriority;
		}
		
		private void runTask(){
			try {
				taskFuture.returnObj = callable.call();
			} catch (Exception e) {
				taskFuture.taskException = e;
				runTaskSem.release();
			}
			taskFuture.isDone = true;
			runTaskSem.release();	
		}
		
/****************************************** TaskFuture ***********************************************/
		private class TaskFuture implements Future<T> {
			private boolean isDone = false;
			private Exception taskException = null;
			T returnObj;
			
			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {				
					try {
						isCancelled = tasksQueue.remove(ThreadPoolTask.this);
						if(isCancelled) {
							isDone = true;	
							runTaskSem.release();
						}
					} 
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				
				return isCancelled;
			}

			@Override
			public T get() throws InterruptedException, ExecutionException {
				runTaskSem.acquire();				

				if(null != taskException) {
					throw new ExecutionException(taskException);
				}

				return returnObj;
			}

			@Override
			public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
				if(runTaskSem.tryAcquire(timeout,unit)) {
					return returnObj;
				} else {
					throw new TimeoutException();
				}
			}

			@Override
			public boolean isCancelled() {
				return isCancelled;
			}

			@Override
			public boolean isDone() {
				return isDone;
			}
		}
	}
}