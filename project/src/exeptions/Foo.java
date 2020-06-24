package exeptions;

import java.io.IOException;

public class Foo {
	public static void main(String args[]) {
		
		/*try {
			func1();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		
		//func1();
		//func2();
		
		/*func3();
		try {
			func4();
		} catch (MyException2 e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		byte[] arr = new byte[100];
	}
	
	public static void func1() throws IOException{
		throw new IOException();
	}
	
	public static void func2() {
		throw new NullPointerException();
	}
	
	public static void func3() throws MyException1{
		throw new MyException1();
	}
	
	public static void func4() throws MyException2{
		throw new MyException2();
	}
	
}
