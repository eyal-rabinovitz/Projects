package genericsWS;

import java.util.*;

public class q4 {
	
	public static void printArray(ArrayList<? extends Number> arr) {
		for(Number i : arr) {
			System.out.println(i);
		}
	}


	public static void main(String args[]) {
		
		/*ArrayList<Integer> intArr = new ArrayList<Integer>();
		ArrayList<Double> doubleArr = new ArrayList<Double>();
		ArrayList<String> strArr = new ArrayList<String>();
		
		printArray(intArr);
		printArray(doubleArr);*/
		//printArray(strArr);
		
		FooReference<Integer> obj1 = new FooReference<Integer>(10);
		
		FooReference<Double> obj2 = new FooReference<Double>(10.5);

		FooReference<String> obj3 = new FooReference<String>("Hello");

		System.out.println(obj1.getObj());
		System.out.println(obj2.getObj());
		System.out.println(obj3.getObj());

		System.out.println(obj1.getClass().getModifiers());
		System.out.println(obj2.obj);
		System.out.println(obj3.getClass());
		
		obj1.setObj(20);
		System.out.println(obj1.getObj());

		ArrayList<Object> objectList;
		ArrayList<String> objectString;
		
		System.out.println(obj1.getClass().getName());

		List rawList;
		List<?> listOfAnyType;
		List<Object> listOfObject = new ArrayList<Object>();
		List<String> listOfString = new ArrayList<String>();
		List<Integer> listOfInteger = new ArrayList<Integer>();

		//rawList = listOfAnyType;
		rawList = listOfString;
		rawList = listOfInteger;
		rawList = listOfObject;
		
		listOfAnyType = listOfString;
		listOfAnyType = listOfInteger;
		
		//listOfObject =  listOfString;
		
	}
}
