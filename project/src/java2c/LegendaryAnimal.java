package java2c;

public class LegendaryAnimal extends Cat{
	public LegendaryAnimal() {
		System.out.println("LegendaryAnimal constructor");
	}
	
	static {
		System.out.println("static block LegendaryAnimal");
	}
	
	public void sayHello() {
		System.out.println("legendary hello");
	}
	
	@Override
	public void finalize() throws Throwable {
		System.out.println("finalize LegendaryAnimal with id" + this.ID);
		super.finalize();
	}
	
	@Override
	public String toString() {
		return "LegendaryAnimal with id " + ID;
	}
}
