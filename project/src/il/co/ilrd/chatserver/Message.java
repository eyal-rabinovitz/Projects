package il.co.ilrd.chatserver;

public interface  Message<K, V> {
	public K getKey();
	public V getData();
}