package gatewayserver;

public interface  Message<K, V> {
	public K getKey();
	public V getData();
}