package il.co.ilrd.executor;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Executor {
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		
		executor.execute(new Runnable() {
		    public void run() {	    		
		    		System.out.println("Asynchronous task");
		    }
		});
		executor.shutdown();
		
		
		ExecutorService executor2 = Executors.newFixedThreadPool(3);
		for(int i = 0; i < 3; ++i) {
			Future<Integer> result = executor2.submit(new MyCallable());
			System.out.println(result.get());
		}
		executor2.shutdown();

		
		ExecutorService executor3 = Executors.newCachedThreadPool();
		
		ExecutorService executor4 = Executors.newScheduledThreadPool(10);
	}
    
}
