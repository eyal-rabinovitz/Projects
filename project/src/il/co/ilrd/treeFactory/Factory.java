package il.co.ilrd.treeFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Factory<T, K, D> {
	private Map<K, Function<D, ? extends T>> map= new HashMap<>();
	
	public void add(K key, Function<D, ? extends T> func) {
		map.put(key, func);
	}

	public T create(K key, D data) {
		return map.get(key).apply(data);
	}
	
	public T create(K key) {
		return map.get(key).apply(null);
	}
/*
	public static void main(String[] args)
	{ 
		Factory<Animal, Integer, Integer> factory = new Factory<Animal, Integer, Integer>();
		Function<Integer, Animal> createAnimal = (animal) -> new Animal();
		
		factory.add(0, createAnimal);
		Animal lion = factory.create(0);
		System.out.println(lion.toString());
		
		factory.add(1, new Function<Integer, Lion>() {
			@Override
			public Lion apply(Integer i){
				return new Lion();
			}
		});
		System.out.println(factory.create(1).toString());

		
		factory.add(2, Bird::getInstance);
		System.out.println(factory.create(2).toString());
		
		Dog dog = new Dog();
		factory.add(3, dog::getInstanceNotStatic);
		System.out.println(factory.create(3).toString());	
		
		ArrayList<Animal> animalList = new ArrayList<Animal>();
		animalList.add(new Dog());
		animalList.add(new Dog());

		Bird bird = new Bird();
		Factory<Animal, Integer, Bird> factory2 = new Factory<>();
		factory2.add(4, Bird::getInstance2);
		System.out.println(factory2.create(4, bird).toString());	

		
		Function<Integer, Double> half = a -> a / 2.0; 
		
		System.out.println(half.apply(10)); 
	}*/
}