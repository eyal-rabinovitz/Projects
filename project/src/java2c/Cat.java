package java2c;

public class Cat extends Animal{
	private int num_masters = 5;
	private String colors;
	
	public Cat() {
		this("black");
		System.out.println("cat constructo");
		this.num_masters = 2;
	}
	
	static {
		System.out.println("static block cat");
	}
	
	public Cat(String colors) {
		this.colors = colors;
		System.out.println("cat constructo with color" + colors);
	}
	
	@Override
	public void finalize() throws Throwable {
		System.out.println("finalize cat with id" + this.ID);
		super.finalize();
	}
	
	@Override
	public String toString() {
		return "cat with id " + ID;
	}

}