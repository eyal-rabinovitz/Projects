package il.co.ilrd.observer;

import java.util.function.Consumer;

public class Observer <T> {
	
	private Callback<T> callback;
	
	{
		Consumer<T> update = (a) -> System.out.println(a);
		Consumer<T> stopUpdate = (a) -> System.out.println(a);
		callback = new Callback<T>(update, stopUpdate);
	}
	
	public void regitser(Subject<T> subject) {
		subject.register(callback);
	}
	
	public void unregister(Subject<T> subject) {
		subject.unregister(callback);
	}

	public void unregister() {
		callback.getDispatcher().unregister(callback);
	}
	
}
