package il.co.ilrd.genericsWS;

public class FooReference<T> {
	
	T obj;
	
	public FooReference(T obj) {
		this.obj = obj;
	}

	public T getObj() {
		return obj;
	}

	public void setObj(T obj) {
		this.obj = obj;
	}

	
	
}
