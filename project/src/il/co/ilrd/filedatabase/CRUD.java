package il.co.ilrd.filedatabase;

public interface CRUD<K, D> {
	public K create(D data);
	public D read(K key);
	public void update(K key, D data);
	public void delete(K key);
}
