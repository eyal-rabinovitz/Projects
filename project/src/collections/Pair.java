package collections;

import java.util.*;

public class Pair<T,U> implements Map.Entry<T,U> {
	
	private T first;
	private U second;
	
	private Pair(T first, U second) {
		this.first = first;
		this.second = second;
	};
	
	public static<T,U> Pair<T,U> of(T first, U second) {
		return new Pair<T, U>(first, second);
	}

	public static<T,U> Pair<U,T> swap(Pair<T,U> pair) {
		return Pair.of(pair.getValue(), pair.getKey());
	}
	
	 private static<T> Pair<T,T> Compare(T[] array, MinMaxCompare<T> myComp) {
		List<T> maxArray = new ArrayList<T>();
		List<T> minArray = new ArrayList<T>();
		
		for (int i = 0; i < array.length - 1; i+=2) {
			if(myComp.compare(array[i], array[i + 1]) > 0) {
				maxArray.add(array[i]);
				minArray.add(array[i+1]);
			}
			else {
				maxArray.add(array[i+1]);
				minArray.add(array[i]);			
			}
		}
		
		if(array.length % 2 != 0) {
			maxArray.add(array[array.length - 1]);
			minArray.add(array[array.length - 1]);	
		}

		T max = findMax(maxArray, myComp);

		T min = findMin(minArray, myComp);
		
		return new Pair<T, T>(min, max);
	}
	
	private static<T> T findMax(List<T> maxArray, MinMaxCompare<T> myComp) {
		T max = maxArray.get(0);
		for (T t : maxArray) {
			if(myComp.compare(max, t) < 0) {
				max = t;
			}
		}
		
		return max;
	}
	
	private static<T> T findMin(List<T> array, MinMaxCompare<T> myComp) {
		T min = array.get(0);
		for (T t : array) {
			if(myComp.compare(min, t) > 0) {
				min = t;
			}
		}
		
		return min;
	}
	 
	public static<T> Pair<T,T> minMax(T[] array, Comparator<T> comp) {
		MinMaxCompare<T> myComp = (T arg0, T arg1) -> {
			return comp.compare(arg0, arg1);
		};

		return Compare(array, myComp);
	}
	
	public static<T extends Comparable<T>> Pair<T,T> minMax(T[] array) {
		MinMaxCompare<T> myComp = (T arg0, T arg1) -> {
			return arg0.compareTo(arg1);
		};

		return Compare(array, myComp);
	}


/******************************************************************************/
	@Override
	public T getKey() {
		return first;
	};
	
	@Override
	public U getValue() {
		return second;
	};
	
	@Override
	public U setValue(U value) {
		U oldValueU = second;
		this.second = value;

		return oldValueU;
	}

	@Override
	public int hashCode() {
		return first.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(null == obj) {
			return false;
		}
		
		if(!(obj instanceof Pair<?, ?>)) {
			return false;
		}
		
		@SuppressWarnings("unchecked")
		Pair<T, U> pair = (Pair<T, U>)obj;		
		
		if(!(pair.first.getClass().equals(first.getClass())
		 && pair.second.getClass().equals(second.getClass()))) {
		return false;
		}

		return pair.second.equals(second);	
	}
	
	@Override
	public String toString() {
		return "first is = " + this.getKey() + " second is = " + this.getValue();
	}
}
