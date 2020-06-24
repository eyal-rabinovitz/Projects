package collections;

import java.util.*;

public class exercise implements Comparator<Integer>{

	@Override
	public int compare(Integer arg0, Integer arg1) {
		return arg0 - arg1;
	}


	public static void main(String args[]) {
	
		Pair<Integer, String> pair1 = Pair.of(1,"eyal");
		System.out.println(pair1.toString());
		
		Pair<String, Integer> pair2 = Pair.swap(pair1);
		System.out.println(pair2.toString());

		Pair<Integer, String> pair3 = Pair.of(3,"eyal");
		System.out.println(pair3.equals(pair1));
		
		Integer[] array = {10, 20, 30, 50, 80};
		Pair<Integer, Integer> minMax = Pair.minMax(array, new exercise()); 
		System.out.println(minMax.toString());
		
		Pair<Integer, Integer> minMax2 = Pair.minMax(array); 
		System.out.println(minMax2.toString());

		
		int[] array1 = {70,20,30,40,50,60};
		
		for(int i :array1) {
			System.out.println(i);
		}

        List<int[]> list = Arrays.asList(array1); 
        //List<Integer> list1 = Collections.addAll(list1, array1);

		//List<Integer> list1 = new ArrayList<Integer>();
		/*for(int i :array1) {
			list1.add(i);
		}
		
		Collections.sort(list1);
		System.out.println(list1);
		
		HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
		hashMap.put("Sunday", 1);
		hashMap.put("Monday", 2);
		hashMap.put("Tuesday", 3);
		hashMap.put("Wednesday", 4);
		hashMap.put("Thursday", 5);
		hashMap.put("Friday", 6);
		hashMap.put("Saturday", 7);
*/
		/*for(Entry<String, Integer> i :hashMap.entrySet()) {
			System.out.println(i);
			System.out.println(i.getKey());
			System.out.println(i.getValue());
		}
		*/
		DataObject do1 = new DataObject("e", 1);
		DataObject do2 = new DataObject("y", 2);
		DataObject do3 = new DataObject("a", 3);
		DataObject do4 = new DataObject("l", 4);
		
		DataObject[] arr = {do1, do2, do3, do4};
		Map<String, Integer> map = new HashMap<>();
		
		for(DataObject iter: arr) {
			if(map.containsKey(iter.getCode())) {
				
			}
			else {
				map.put(iter.getCode(), iter.getValue());
			}
		}
	}


}
