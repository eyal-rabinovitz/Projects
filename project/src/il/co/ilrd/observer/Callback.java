package il.co.ilrd.observer;

import java.util.function.Consumer;

public class Callback <T> {
	
	private Dispatcher <T> dispatcher;
	private Consumer<T> updateCallback;
	private Consumer<T> stopUpdateCallback;

	public Callback(Consumer<T> updateCallback, Consumer<T> stopUpdateCallback) {
		this.updateCallback = updateCallback;
		this.stopUpdateCallback = stopUpdateCallback;
	}
	
	public void update(T param) {
		updateCallback.accept(param);
	}
	
	public void stopUpdate(T param) {
		stopUpdateCallback.accept(param);
	}
	
	public Consumer<T> getUpdate() {
		return updateCallback;
	}
	
	public Consumer<T> getStopUpdate() {
		return stopUpdateCallback;
	}
	
	public Dispatcher<T> getDispatcher() {
		return dispatcher;
	}

	public void setDispatcher(Dispatcher<T> dispatcher) {
		this.dispatcher = dispatcher;
	}
	
	public void setUpdateCallback(Consumer<T> updateCallback) {
		this.updateCallback = updateCallback;
	}
	
	public void setStopUpdateCallback(Consumer<T> stopUpdateCallback) {
		this.stopUpdateCallback = stopUpdateCallback;
	}
}
