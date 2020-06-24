package hashmap;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import collections.Pair;

public class HashMap<K,V> implements Map<K, V> {
	
	private List<List<Pair<K, V>>> hash;
	private final int capacity;
	private final static int defaultCapacity = 16; 
	private Set<K> keySet;
	private Collection<V> values;
	private Set<Map.Entry<K, V>> entrySet;
	
	public HashMap() {
		this(defaultCapacity);
	}

	public HashMap(int capacity) {
		this.capacity = capacity;
		InitMap();
	}

	@Override
	public void clear() {
		for(List<Pair<K, V>> iterator : hash){
			iterator.clear();
		}		
	}

	@Override
	public boolean containsKey(Object key) {
		return (null != getPairByKey(key));
	}

	@Override
	public boolean containsValue(Object obj) {	
		if(null == obj) {
			return false;
		}
		
		for(V value : values()) {
			if(obj.equals(value)) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public V get(Object key) {
		Pair<K, V> pair = getPairByKey(key);
		
		if(null == pair) {
			return null;
		}
		
		return pair.getValue();
	}

	@Override
	public boolean isEmpty() {
		Iterator<Entry<K, V>> iter =  entrySet().iterator();
		
		return (!iter.hasNext());
	}
	
	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		if(null == entrySet) {
			entrySet = new EntrySet();
		}
		
		return entrySet;
	}
	
	@Override
	public Set<K> keySet() {
		if(null == keySet) {
			keySet = new KeySet();
		}
		
		return keySet;
	}

	@Override
	public Collection<V> values() {
		if(null == values) {
			values = new Values();
		}
		
		return values;
	}
	
	@Override
	public V put(K key, V value) {
		int index = key.hashCode() % capacity;
	
		for (Pair<K, V> iterator : hash.get(index)) {
			if(iterator.getKey().equals(key)) {
				V prevValue = iterator.getValue();
				iterator.setValue(value);
				
				return prevValue;
			}
		}
		
		hash.get(index).add(Pair.of(key, value));
		
		return null;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> hashMap) {
		for(Entry<? extends K, ? extends V> iterator : hashMap.entrySet()) {
			put(iterator.getKey(), iterator.getValue());
		}	
	}

	@Override
	public V remove(Object key) {
		Pair<K, V> pairToRemove = getPairByKey(key);
		
		if(null != pairToRemove) {
			List<Pair<K, V>> bucket = getBucketByKey(key);
			V prevValue = this.get(key);
			
			if(!bucket.remove(pairToRemove)) {
				return null;
			}

			return prevValue;
		}

		return null;
	}
	
	@Override
	public int size() {
		int size = 0;
		
		for(List<Pair<K, V>> iterator : hash) {
			size+= iterator.size();			
		}
		
		return size;
	}

	private void InitMap() {
		hash = new ArrayList<>();
		
		for(int i = 0; i < capacity; ++i) {
			hash.add(new ArrayList<>());
		}
	}
	
	private List<Pair<K, V>> getBucketByKey(Object key) {
		if(null == key) {
			key = 0;
		}
		
		int index = key.hashCode() % capacity;
		
		return hash.get(index);
	}
	
	private Pair<K, V> getPairByKey(Object key) {
		List<Pair<K, V>> bucket = getBucketByKey(key);
		
		for(Pair<K, V> pair : bucket) {
			if(pair.getKey().equals(key)) {
				return pair;
			}
		}
		
		return null;
	}
	
	private class EntrySet extends AbstractSet<Map.Entry<K, V>>{
		
		@Override
		final public Iterator<Map.Entry<K, V>> iterator() {
			return new EntryIterator();
		}

		@Override
		public int size() {
			return 0;
		}
		
		private class EntryIterator implements Iterator<Map.Entry<K, V>> {

			private Iterator<List<Pair<K, V>>> outer = hash.iterator();
			private Iterator<Pair<K, V>> inner = outer.next().iterator();
			
			{
				while(!inner.hasNext() && outer.hasNext()) {
					inner = outer.next().listIterator();
				}
			}
			
			@Override
			public boolean hasNext()  {
				if(inner.hasNext()) {
					return true;
				}
				
				while(outer.hasNext()) {
					if(!outer.next().isEmpty()) {
						return true;
					}
				}
				
				return false;
			}
			
			@Override
			public Entry<K, V> next() {
				Map.Entry<K, V> entry = inner.next();
				
				while(!inner.hasNext() && outer.hasNext()) {
					inner = outer.next().iterator();
				}

				return entry;
			}
		}
	}

	private class KeySet extends AbstractSet<K> implements Iterator<K>{

		private Iterator<Map.Entry<K, V>> iterator = new EntrySet().iterator();
		
		@Override
		final public Iterator<K> iterator() {
			return new KeySet();
		}
				
		@Override	
		public boolean hasNext() {
			return iterator.hasNext();
		}

		public K next() {
			return iterator.next().getKey();
		}

		@Override
		public int size() {
			return 0;
		}
		
	}

	private class Values extends AbstractSet<V> implements Iterator<V>{

		private Iterator<Map.Entry<K, V>> iterator = new EntrySet().iterator();
		
		@Override
		final public Iterator<V> iterator() {
			return new Values();
		}
				
		@Override	
		public boolean hasNext() {
			return iterator.hasNext();
		}

		public V next() {
			return iterator.next().getValue();
		}

		@Override
		public int size() {
			return 0;
		}
		
	}
}
