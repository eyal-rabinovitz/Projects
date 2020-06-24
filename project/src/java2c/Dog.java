package java2c;

public class Dog extends Animal{
	private int num_legs = 4;

	public Dog() {
		super(2);
		System.out.println("dog constructo");
	}
	
	static {
		System.out.println("static block dog");
	}
	
	public void sayHello() {
		System.out.println("dog hello");
		System.out.println("i have " + num_legs+ " legs");
	}
	
	{
		System.out.println("instance initialization block dog");
	}
	
	@Override
	public String toString() {
		return "dog with id " + ID;
	}
	
	@Override
	public void finalize() throws Throwable {
		System.out.println("finalize dog with id" + this.ID);
		super.finalize();
	}
	
}
