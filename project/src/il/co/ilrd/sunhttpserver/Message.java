package il.co.ilrd.sunhttpserver;

public interface  Message<K, V> {
	public K getKey();
	public V getData();
}