package il.co.ilrd.multithreaded.pingpongbroadcast2;

import java.util.Scanner;

public class InputDetector implements Runnable
{
    private String input;
    private Scanner scan;
	private String breakInput;
	private Runnable stopFunc;

    public InputDetector(Runnable stopFunc, String breakInput) {
        input = "";
        scan = new Scanner(System.in);
        this.stopFunc = stopFunc;
        this.breakInput = breakInput;
    }

    public void start() {
       new Thread(this).start();
    }
    
    @Override
    public void run() {
        while (!(input.equals(breakInput))) {
            input = scan.nextLine();
        }
        stopFunc.run();
    }

    public String getInput() {
        return input;
    }
}