package hashmap;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

class HashMapTest {

	@Test
	void testHashMap() {
	}

	@Test
	void testHashMapInt() {
	}
	
	@Test
	void testClear() {
		HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
		hashMap.put(0, "e");
		hashMap.put(1, "y");
		hashMap.put(2, "a");
		
		hashMap.clear();

		assertEquals(false, hashMap.containsKey(0));

	}

	@Test
	void testContainsKey() {
		HashMap<Integer, String> hashMap = new HashMap<Integer, String>();

		hashMap.put(0, "e");
		hashMap.put(1, "y");
		hashMap.put(2, "a");
		
		assertEquals(true, hashMap.containsKey(0));
		assertEquals(true, hashMap.containsKey(1));
		assertEquals(true, hashMap.containsKey(2));
		assertEquals(false, hashMap.containsKey(3));
	}

	@Test
	void testContainsValue() {
		HashMap<Integer, String> hashMap = new HashMap<Integer, String>();

		hashMap.put(0, "e");
		hashMap.put(1, "y");
		hashMap.put(2, "a");
		
		assertEquals(true, hashMap.containsValue("y"));
		assertEquals(true, hashMap.containsValue("a"));
		assertEquals(true, hashMap.containsValue("e"));
		assertEquals(false, hashMap.containsValue("l"));
	}

	@Test
	void testEntrySet() {
		HashMap<Integer, String> hashMap = new HashMap<Integer, String>();

		hashMap.put(0, "e");
		hashMap.put(1, "y");
		hashMap.put(2, "a");

		Set<Map.Entry<Integer, String>> entry = hashMap.entrySet();
		
/**/	assertEquals("[first is = 0 second is = e, first is = 1 second is = y, first is = 2 second is = a]", entry.toString());/**//**//**/
		//assertEquals(true, entry.contains(0));
	}

	@Test
	void testGet() {
		HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
		
		hashMap.put(0, "e");
		assertEquals("e", hashMap.get(0));
		
		hashMap.put(1, "y");
		assertEquals("y", hashMap.get(1));
	}

	@Test
	void testIsEmpty() {
		HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
		assertEquals(true, hashMap.isEmpty());
		
		hashMap.put(0, "e");
		assertEquals(false, hashMap.isEmpty());

		hashMap.remove(0);
		assertEquals(true, hashMap.isEmpty());
	}

	@Test
	void testKeySet() {
		HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
		hashMap.put(0, "e");
		hashMap.put(1, "y");
		hashMap.put(2, "a");
		
		Set<Integer> keySet = hashMap.keySet();
		
		assertEquals("[0, 1, 2]", keySet.toString());
		keySet.size();
	}

	@Test
	void testPut() {
		HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
		
		assertEquals(null, hashMap.put(0, "e"));
		assertEquals("e", hashMap.put(0, "y"));
		assertEquals("y", hashMap.put(0, "a"));
		assertEquals("a", hashMap.put(0, "l"));

		assertEquals(null, hashMap.put(1, "r"));		
	}
	

	@Test
	void IteratorTest() {
		HashMap<Integer, String> hashMap1 = new HashMap<Integer, String>(7);
	
		hashMap1.put(10, "value 1");
		hashMap1.put(16, "value 2");
		hashMap1.put(2, "value 3");
		hashMap1.put(0, "value 3");
		hashMap1.put(1, "value 2");
		hashMap1.put(11, "value 3");
		hashMap1.put(18, "value 3");
		
		for(Map.Entry<Integer, String> entry: hashMap1.entrySet()) {
			System.out.println(entry.getKey());
		}
	}




	@Test
	void testPutAll() {
		HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
		HashMap<Integer, String> hashMap2 = new HashMap<Integer, String>();

		hashMap.put(0, "e");
		hashMap.put(1, "y");
		hashMap.put(2, "a");
		
		hashMap2.putAll(hashMap);
		
		assertEquals(3, hashMap2.size());
		assertEquals(true, hashMap2.containsKey(0));
		assertEquals(true, hashMap2.containsKey(1));
		assertEquals(true, hashMap2.containsKey(2));
		assertEquals(false, hashMap2.containsKey(3));
	}

	@Test
	void testRemove() {
		HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
		assertEquals(null, hashMap.put(0, "e"));
		assertEquals("e", hashMap.remove(0));
	}

	@Test
	void testSize() {
		HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
		assertEquals(0, hashMap.size());

		hashMap.put(0, "e");
		hashMap.put(1, "e");
		hashMap.put(2, "e");		
		assertEquals(3, hashMap.size());
		
		hashMap.put(4, "e");		
		assertEquals(4, hashMap.size());
	}

	@Test
	void testValues() {
		HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
		assertEquals(0, hashMap.size());

		hashMap.put(0, "e");
		hashMap.put(1, "y");
		hashMap.put(2, "a");
		
		assertEquals("[e, y, a]", hashMap.values().toString());
	}

}
