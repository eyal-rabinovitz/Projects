package java2c;

class Animal {
	
	static {
	System.out.println("static block animal 1");
	}
	
	{
		System.out.println("instance initialization block animal");
	}
	
	int ID;
	private int num_legs = 5;
	public static int counter = 0;
	private int num_masters = 1;

	public Animal() {
		System.out.println("animal constructor");
		this.ID = ++counter;
		sayHello();
		showCounter();
		System.out.println(toString());
		System.out.println(super.toString());
	}
	
	public Animal(int num_master) {
		System.out.println("animal constructor int num_master");
		this.ID = ++counter;
		this.num_masters = num_master;
	}
	
	public void sayHello() {
		System.out.println("animal hello");
		System.out.println("i have " + num_legs+ " legs");
	}
	
	public static void showCounter() {
		System.out.println(counter);
	}
	
	public int getNumMasters() {
		return this.num_masters;
	}
	
	@Override
	public String toString() {
		return "animal with id " + ID;
	}
	
	@Override
	public void finalize() throws Throwable {
		System.out.println("finalize animal with id" + this.ID);
		super.finalize();
	}
	static {
		System.out.println("static block animal 2");
	}
}
