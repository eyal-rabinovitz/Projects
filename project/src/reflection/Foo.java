package reflection;

public class Foo implements Comparable  {
	public int number = 0;
	
	
	public Foo(int number) {
		this.number = number;
	}
	
	public int getNumber() {
		return number;
	}
	
	public void method1()  { 
        System.out.println("method 1"); 
    }

	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	} 
}
