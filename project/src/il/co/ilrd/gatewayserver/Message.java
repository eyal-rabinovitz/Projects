package il.co.ilrd.gatewayserver;

public interface  Message<K, V> {
	public K getKey();
	public V getData();
}