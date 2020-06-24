package il.co.ilrd.observer;

import java.util.ArrayList;
import java.util.List;

public class Dispatcher<T> {
	private List<Callback<T>> callbackList = new ArrayList<>();
	
	public void register(Callback<T> callback) {
		callback.setDispatcher(this);
		callbackList.add(callback);
	}
	
	public void unregister(Callback<T>  callback) {
		callbackList.remove(callback);
		callback.setDispatcher(null);
	}
	
	public void updateAll(T param) {
		for(Callback<T> iterCallback : callbackList) {
			iterCallback.update(param);
		}
	}
	
	public void stopUpdate(T param) {
		for(Callback<T> iterCallback : callbackList) {
			iterCallback.stopUpdate(param);
		}		
	}
}
