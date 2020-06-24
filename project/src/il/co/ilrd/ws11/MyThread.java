package il.co.ilrd.ws11;

import java.util.concurrent.locks.ReentrantLock;

public class MyThread extends Thread {
	private static boolean runnable = true;
	ReentrantLock re; 
    public void run(){
    	int i = 0;
    	while(runnable) {
    		System.out.println("MyThread running" + i);
    		++i;
    	}
    }
    
    public static void stopThred() {
		runnable = false;
	}
  }