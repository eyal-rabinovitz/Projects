package il.co.ilrd.observer;

public class Main {
	public static void main(String[] args) {
		System.out.println("hi");
		Subject<Integer> s = new Subject<>();
		Observer<Integer> o1 = new Observer<>();
		Observer<Integer> o2 = new Observer<>();

		o1.regitser(s);
		o2.regitser(s);
		s.updateAll(1);
		
		s.stopUpdate(3);
		
		o1.unregister(s);
		s.updateAll(2);

	}
}
