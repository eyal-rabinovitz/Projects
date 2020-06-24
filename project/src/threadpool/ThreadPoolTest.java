package threadpool;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.Callable;

import org.junit.jupiter.api.Test;

class ThreadPoolTest {

	@Test
	void testThreadPool() {
		//fail("Not yet implemented");
	}

	@Test
	void testThreadPoolInt() {
		//fail("Not yet implemented");
	}

	@Test
	void testSubmitTaskCallableOfT() {
		ThreadPool tp = new ThreadPool();
		Callable<Integer> callable = new Callable<Integer>() {

			@Override
			public Integer call() throws Exception {
				System.out.println("SubmitTask = Callable ");
				return 10;
			}
		};
		
		tp.submitTask(callable);
	}	

	@Test
	void testSubmitTaskCallableOfTTaskPriority() {
		ThreadPool tp = new ThreadPool();
		Callable<Integer> callable = new Callable<Integer>() {

			@Override
			public Integer call() throws Exception {
				System.out.println("SubmitTask = Callable + TaskPriority ");
				return 10;
			}
		};
		
		tp.submitTask(callable, ThreadPool.TaskPriority.MIN);
	}

	@Test
	void testSubmitTaskRunnableTaskPriority() {
		ThreadPool tp = new ThreadPool();
		
		Runnable runnable = new Runnable() {
			
			@Override
			public void run() {
				System.out.println("SubmitTask = Runnable + TaskPriority ");
				
			}
		};
		
		tp.submitTask(runnable, ThreadPool.TaskPriority.MIN);
	}

	@Test
	void testSubmitTaskRunnableTaskPriorityT() {
		ThreadPool tp = new ThreadPool();
		Integer x = 10;
		Runnable runnable = new Runnable() {
			
			@Override
			public void run() {
				System.out.println("SubmitTask = Runnable + TaskPriority + T ");
				
			}
		};
		
		tp.submitTask(runnable, ThreadPool.TaskPriority.MIN, x);
	}

	@Test
	void testSetNumberOfThread() {
		ThreadPool tp = new ThreadPool();
		Callable<Integer> callable = new Callable<Integer>() {

			@Override
			public Integer call() throws Exception {
				System.out.println("SubmitTask = Callable ");
				return 10;
			}
		};
		
		tp.submitTask(callable);
		tp.submitTask(callable);
		tp.submitTask(callable);
		tp.submitTask(callable);

	}

	@Test
	void testExecute() {
		//fail("Not yet implemented");
	}

	@Test
	void testPause() {
		//fail("Not yet implemented");
	}

	@Test
	void testResume() {
		//fail("Not yet implemented");
	}

	@Test
	void testShutdown() {
		//fail("Not yet implemented");
	}

	@Test
	void testAwaitTermination() {
		//fail("Not yet implemented");
	}

}
