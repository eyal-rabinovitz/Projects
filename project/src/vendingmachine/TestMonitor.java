package vendingmachine;

public class TestMonitor implements Monitor {
	@Override
	public void print(String printOut) {
		System.out.print(printOut);
	}

}
