package il.co.ilrd.ws11;

public class MyRunnable implements Runnable{
	private static boolean runnable = true;

	@Override
    public void run() {
		int i = 0;
    	while(runnable) {
    		System.out.println("MyRunnable running" + i);
    		++i;
    	}
    }
	
    public static void stopThred() {
		runnable = false;
	}
}
