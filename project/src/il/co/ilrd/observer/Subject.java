package il.co.ilrd.observer;

public class Subject<T> {
	
	private Dispatcher<T> dispatcher = new Dispatcher<>();
	
	public void register(Callback<T> callback) {
		dispatcher.register(callback);
	}
	public void unregister(Callback<T> callback) {
		dispatcher.unregister(callback);
	}
	public void updateAll(T param) {
		dispatcher.updateAll(param);
	}
	public void stopUpdate(T param) {
		dispatcher.stopUpdate(param);
	}
}
