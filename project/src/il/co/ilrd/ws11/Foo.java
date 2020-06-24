package il.co.ilrd.ws11;

public class Foo<T> {

	private T t;
	
	public T get(){
		return this.t;
	}
	
	public <E> void set(T t1){
		this.t= t1;
	}
	
	public static <T> boolean isEqual(Foo<T> g1, Foo<T> g2){
		return g1.get().equals(g2.get());
	}
}