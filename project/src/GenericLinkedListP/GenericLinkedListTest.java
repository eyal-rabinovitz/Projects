package GenericLinkedListP;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GenericLinkedListTest {

	GenericLinkedList<Integer> listInteger;
	GenericLinkedList<Integer> listInteger2;
	GenericLinkedList<String> listString; 
	
	@BeforeEach
	void init() {
		listInteger = new GenericLinkedList<>();
		listInteger2 = new GenericLinkedList<>();
		listString = new GenericLinkedList<>();
	}
	
	@Test
	void testPushFront() {
		assertEquals(0, listInteger.size());
		listInteger.pushFront(5);
		assertEquals(1, listInteger.size());
		listInteger.pushFront(100);
		assertEquals(2, listInteger.size());
		listInteger.pushFront(200);
		assertEquals(3, listInteger.size());
		
		assertEquals(0, listInteger2.size());
		listInteger2.pushFront(4);
		assertEquals(1, listInteger2.size());
		listInteger2.pushFront(150);
		assertEquals(2, listInteger2.size());
		listInteger2.pushFront(250);
		assertEquals(3, listInteger2.size());
		
		assertEquals(0, listString.size());
		listString.pushFront("David");
		assertEquals(1, listString.size());
		listString.pushFront("Ok");
		assertEquals(2, listString.size());
		listString.pushFront("Cupid");
		assertEquals(3, listString.size());
		
	}
	
	@Test
	void testPopFront() {
		
		testPushFront();
		assertEquals(listInteger.popFront(), 200);
		assertEquals(2, listInteger.size());
		assertEquals(listInteger.popFront(), 100);
		assertEquals(1, listInteger.size());
		assertEquals(listInteger.popFront(), 5);
		assertEquals(0, listInteger.size());
		
		assertEquals(listString.popFront(), "Cupid");
		assertEquals(2, listString.size());
	}

	@Test
	void testIsEmpty() {
		assertEquals(true, listInteger.isEmpty());
		testPushFront();
		assertEquals(false, listInteger.isEmpty());
	}
	
	@Test
	void testIter() {
		Iterator<Integer> iter = listInteger.iterator();
		assertEquals(null, iter.next());
		testPushFront();
		iter = listInteger.iterator();
		assertEquals(200, iter.next());
		
		Iterator<String> iter2 = listString.iterator();
		assertEquals("Cupid", iter2.next());
	}
	

	@Test
	void testFind() {
		assertEquals(null, listInteger.find(5));
		testPushFront();
		Iterator<Integer> iter = listInteger.iterator();
		Iterator<String> iter2 = listString.iterator();
		iter = listInteger.find(100);
		assertEquals(100, iter.next());
		iter2 = listString.find("David");
		assertEquals("David", iter2.next());
	}
	
	@Test
	void testReverse() {
		testPushFront();
		GenericLinkedList<Integer> reverse = 
						GenericLinkedList.newReverse(listInteger);
		assertEquals(reverse.popFront(), 5);
		assertEquals(reverse.popFront(), 100);
		assertEquals(reverse.popFront(), 200);

		GenericLinkedList<String> reverseString = 
				GenericLinkedList.newReverse(listString);
		assertEquals(reverseString.popFront(), "David");
		assertEquals(reverseString.popFront(), "Ok");
		assertEquals(reverseString.popFront(), "Cupid");
	}
	
	@Test
	void testFailFast() {
		testPushFront();
		Iterator<Integer> iter = listInteger.iterator();
		listInteger.popFront();
		try{
			iter.next();
		}
		
		catch(ConcurrentModificationException e) {
			System.out.println("Exception thrown");
		}
	}
	
	@Test
	void testMerge() {
		testPushFront();
		GenericLinkedList<Integer> merge = 
						GenericLinkedList.merge(listInteger, listInteger2);
		assertEquals(merge.popFront(), 200);
		assertEquals(merge.popFront(), 100);
		assertEquals(merge.popFront(), 5);
		assertEquals(merge.popFront(), 250);
		assertEquals(merge.popFront(), 150);
		assertEquals(merge.popFront(), 4);
		}
	
	@AfterEach
	void slistDestroy() {
		listString = null;
		listInteger = null;
		listInteger2 = null;
	}

}
