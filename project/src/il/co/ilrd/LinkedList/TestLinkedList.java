package il.co.ilrd.LinkedList;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TestLinkedList {

	
	
	@Test
	void testPushFront() {
		SinglyLinkedList list = new SinglyLinkedList();
		list.pushFront(10);
		Iterator iter = list.begin();
		assertEquals(10, iter.next());	

		list.pushFront(20);
		iter = list.begin();
		assertEquals(20, iter.next());	
		assertEquals(10, iter.next());	

		list.pushFront(30);
		iter = list.begin();
		assertEquals(30, iter.next());	
		assertEquals(20, iter.next());	
		assertEquals(10, iter.next());	
	}

	@Test
	void testPopFront() {
		SinglyLinkedList list = new SinglyLinkedList();
		list.pushFront(10);
		list.pushFront(20);
		list.pushFront(30);
		list.pushFront(40);
		
		list.popFront();
		Iterator iter = list.begin();
		assertEquals(30, iter.next());	

		list.popFront();
		iter = list.begin();
		assertEquals(20, iter.next());	

		list.popFront();
		iter = list.begin();
		assertEquals(10, iter.next());	
		
		list.popFront();
		iter = list.begin();
		assertNull(iter.next());
		
		list.popFront();
		iter = list.begin();
		assertNull(iter.next());
	}

	@Test
	void testSize() {
		SinglyLinkedList list = new SinglyLinkedList();
		assertEquals(0, list.size());
		
		list.pushFront(10);
		assertEquals(1, list.size());
		
		list.popFront();
		assertEquals(0, list.size());	
		
		list.pushFront(10);
		list.pushFront(20);
		list.pushFront(30);
		list.pushFront(40);
		assertEquals(4, list.size());	

	}

	@Test
	void testIsEmpty() {
		SinglyLinkedList list = new SinglyLinkedList();
		assertEquals(true, list.isEmpty());	
		
		list.pushFront(10);
		assertEquals(false, list.isEmpty());
		
		list.popFront();
		assertEquals(true, list.isEmpty());	
	}

	@Test
	void testBegin() {
		SinglyLinkedList list = new SinglyLinkedList();
		list.pushFront(10);
		list.pushFront(20);
		list.pushFront(30);
		Iterator iter = list.begin();
		assertEquals(30, iter.next());	

	}

	@Test
	void testFind() {
		SinglyLinkedList list = new SinglyLinkedList();
		list.pushFront(10);
		list.pushFront(20);
		list.pushFront(30);
		Iterator iter = list.find(20);
		assertEquals(20, iter.next());	

		iter = list.find(10);
		assertEquals(10, iter.next());	

		iter = list.find(30);
		assertEquals(30, iter.next());	
		}

}
