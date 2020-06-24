package reflection;

import java.lang.reflect.Method;

import com.sun.jdi.InterfaceType;

import java.lang.reflect.Field; 
import java.lang.reflect.Constructor; 
  
public class exercise {
	public static void main(String args[]) throws NoSuchMethodException, SecurityException {
		Foo f = new Foo(10);
		System.out.println(f.getClass());

		Field[] fields = Foo.class.getFields();
		System.out.println(fields[0]);

		Method[] methods = Foo.class.getMethods(); 
		System.out.println(methods[1]);

		Class[]  i = Foo.class.getInterfaces();
		System.out.println(i[0]);

		
		Class<Foo> class1 = Foo.class;
		Constructor<Foo> constructor = class1.getConstructor();
		
		//Foo object = (Foo)constructor.newInstance();
		//System.out.println(object.getClass());
	}
}

