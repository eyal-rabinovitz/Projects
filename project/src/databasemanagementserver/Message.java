package databasemanagementserver;

public interface  Message<K, V> {
	public K getKey();
	public V getData();
}