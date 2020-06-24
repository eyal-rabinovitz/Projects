package hashmap;

import collections.Pair;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Sharon<K,V> implements Map<K, V>  {

	private List<List<Pair<K, V>>> entries;
	private int capacity = 16;
	
	public Sharon() {
		InitList();
	}
	
	public Sharon(int capacity) {
		this.capacity = capacity;
		InitList();
	}
	
	public void InitList() {
		entries = new ArrayList<List<Pair<K, V>>>();
		
		for(int i = 0; i < capacity; ++i) {
			entries.add(new LinkedList<Pair<K, V>>());
		}
	}
	
	@Override
	public void clear() {
		for(int i = 0; i < capacity; ++i) {
			entries.get(i).clear();
		}
	}

	@Override
	public boolean containsKey(Object arg0) {
		int index = getBucket(arg0);
		List<Pair<K, V>> innerList = entries.get(index);
		
		for(Pair<K, V> pair : innerList) {
			if(arg0 == pair.getKey()) {
				return true;
			}
		}
		
		return false;
	}
	
	public int getBucket(Object arg0) {
		return arg0.hashCode() % capacity;
	}

	@Override
	public boolean containsValue(Object arg0) {
		for(List<Pair<K, V>> innerList : entries) {
			for(Pair<K, V> pair : innerList) {
				if(arg0 == pair.getValue()) {
					return true;
				}
			}
		}
			
		return false;
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		return new EntrySet();
	}

	@Override
	public V get(Object arg0) {
		int index = getBucket(arg0);
		List<Pair<K, V>> innerList = entries.get(index);
		
		for(Pair<K, V> pair : innerList) {
			if(arg0 == pair.getKey()) {
				return pair.getValue();
			}
		}		
		return null;
	}

	@Override
	public boolean isEmpty() {
		Iterator<Entry<K, V>> iter =  entrySet().iterator();
		
		return (!iter.hasNext());
	}

	@Override
	public Set<K> keySet() {
		return new KeySet(); 
	}

	@Override
	public V put(K arg0, V arg1) {
		
		V valueToReturn = get(arg0);
		int index = getBucket(arg0);
		
		if(!containsKey(arg0)) {
			Pair<K, V> newPair = Pair.of(arg0, arg1);
			entries.get(index).add(newPair);
		}
		
		else {
			List<Pair<K, V>> innerList = entries.get(index);
			for(Pair<K, V> pair : innerList) {
				if(arg0.equals(pair.getKey())) {
					pair.setValue(arg1);
				}
			}
		}
		
		return valueToReturn;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> arg0) {		
		for(Map.Entry<? extends K, ? extends V> entry: arg0.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public V remove(Object arg0) {
		if(!containsKey(arg0)) {
			return null;
		}
		
		V valueToReturn = get(arg0);
		int index = getBucket(arg0);
		List<Pair<K, V>> innerList = entries.get(index);
		
		for(Pair<K, V> pair : innerList) {
			if(arg0.equals(pair.getKey())) {
				innerList.remove(pair);
				break;
			}
		}
		
		return valueToReturn;
	}

	@Override
	public int size() {
		int size = 0;

		for(List<Pair<K, V>> inerList : entries) {
			size += inerList.size();
		}
		
		return size;
	}

	@Override
	public Collection<V> values() {
		return new ValSet();
	}

	private class EntrySet extends AbstractSet<Map.Entry<K, V>>{

		@Override
		final public Iterator<Map.Entry<K, V>> iterator() {
			return new EntryIterator();
		}

		@Override
		public int size() {
			return size();
		}
		
		private class EntryIterator implements Iterator<Map.Entry<K, V>>{
			Iterator<List<Pair<K, V>>> outer = entries.listIterator();
			Iterator<Pair<K, V>> inner = outer.next().listIterator();
			
			{	
				while((!inner.hasNext()) && (outer.hasNext())) {
					inner = outer.next().listIterator();
				}
			}
			
			@Override
			public boolean hasNext() {
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
				Entry<K, V> entry = inner.next();
				while((!inner.hasNext()) && (outer.hasNext())) {
						inner = outer.next().listIterator();
				}
					
				return entry;
			}	
		}
	}
	
	private class KeySet extends AbstractSet<K> {
		@Override
		public Iterator<K> iterator() {
			return new KeyIterator();
		}

		@Override
		public int size() {
			return size();
		}	
	}
	
	private class KeyIterator implements Iterator<K>{
		Iterator<Map.Entry<K, V>> iter = new EntrySet().iterator();

		@Override
		public boolean hasNext() {
			return (iter.hasNext());
		}

		@Override
		public K next() {
			return iter.next().getKey();
		}
	}
		
	private class ValSet extends AbstractSet<V> {
		@Override
		public Iterator<V> iterator() {
			return new ValIterator();
		}

		@Override
		public int size() {
			return size();
		}	
	}
		
	private class ValIterator implements Iterator<V>{
		Iterator<Map.Entry<K, V>> iter = new EntrySet().iterator();

		@Override
		public boolean hasNext() {
			return (iter.hasNext());
		}

		@Override
		public V next() {
			return iter.next().getValue();
		}
	}	
}