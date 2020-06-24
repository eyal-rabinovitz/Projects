package java2c;

public class Main {
	public static void foo(Animal a) {
		System.out.println(a.toString());		
	}
	
	//@suppressWarnings...
	public static void main(String args[]) throws Throwable {
		Object object;
		Animal animal = new Animal();
		Dog dog = new Dog();
		Cat cat = new Cat();
		LegendaryAnimal la = new LegendaryAnimal();
		
		Animal.showCounter();
		
		System.out.println("animal id = " + animal.ID);
		System.out.println("dog id = " + ((Animal)dog).ID);
		System.out.println("cat id = " + ((Animal)cat).ID);
		System.out.println("la id = " + ((Animal)la).ID);

		Animal[] arr = { new Dog(), new Cat(), new Cat("white"), new LegendaryAnimal(), new Animal()};
		
		for(Animal a: arr) {
			a.sayHello();
			System.out.println(a.getNumMasters());
		}
	
		for(Animal a: arr) {
			foo(a);
		
		}
		la.finalize();
	}

}
