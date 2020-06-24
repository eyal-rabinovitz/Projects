package il.co.ilrd.GenericLinkedListP;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

/******************************************************************************/
/*--------------------------GenericLinkedList---------------------------------*/

public class GenericLinkedList <E> implements Iterable<E>{
	
	private Node<E> head = new Node<>(null, null);
	private transient int modeCount = 0;	
	
/******************************************************************************/
/*--------------------------Node----------------------------------------------*/
	
	private static class Node <E>{
		private Node<E> next;
		private E data;

		public Node(E data, Node<E> next) {		
			this.next = next;
			this.data = data;
		}
	} 
	
/******************************************************************************/
/*--------------------------ListIteratorImpl----------------------------------*/

	// Inner class ListIteratorImpl
	private class ListIteratorImpl implements Iterator<E>{
		private Node<E> currentNode = head;
		private int expectedModeCount = modeCount;

		@Override
		public boolean hasNext() {
			return (currentNode.next != null);
		}

		@Override
		public E next() {
			checkForComodification();
			E data = currentNode.data;
			currentNode = currentNode.next;
			
			return data;
		}
		
		final void checkForComodification() {
		    if (modeCount != expectedModeCount)
		        throw new ConcurrentModificationException();
		}
	}

/*--------------------------merge---------------------------------------------*/
		
	public static <E> GenericLinkedList<E> merge(GenericLinkedList<E> l1, 
												 GenericLinkedList<E> l2) {
		GenericLinkedList<E> mergedList = new GenericLinkedList<>();
		
		for(E data : l1) {
			mergedList.pushFront(data);
		}
		
		for(E data : l2) {
			mergedList.pushFront(data);
		}
		
		return newReverse(mergedList);
	}
	
/*--------------------------newReverse----------------------------------------*/
	
	public static <E> GenericLinkedList<E> newReverse(GenericLinkedList<E> l1) {
		GenericLinkedList<E> reverseList = new GenericLinkedList<>();
		
		for(E data : l1) {
			reverseList.pushFront(data);
		}
		
		return reverseList;
	}
	
/*--------------------------pushFront-----------------------------------------*/
			
	public void pushFront(E data) {
		head = new Node<E>(data, head);
		++modeCount;
	}
	
/*--------------------------popFront------------------------------------------*/	
	
	public E popFront() {
		if(this.isEmpty()) {
			return null;
		}
		
		++modeCount;
		E poppedData = head.data;
		head = head.next;
		
		return poppedData;
	}
	
/*--------------------------isEmpty-------------------------------------------*/	

	public boolean isEmpty() {
		return (null == head.next);	
	}
	
/*--------------------------size----------------------------------------------*/	

	public int size() {
		int size = 0;
		
		for(E element : this) {
			++size;
			}
		
		return size;
	}
	
/*--------------------------iterator------------------------------------------*/		
	
	@Override
	public Iterator<E> iterator() {
		return new ListIteratorImpl();
	}
	
/*--------------------------find----------------------------------------------*/		
	
	public Iterator<E> find(E data) {
		Iterator<E> iterRunner = iterator();
		
		for (E dataRunner : this) {
			if(dataRunner.equals(data)) {
				return iterRunner;
			}
			iterRunner.next();
		}
		
		return null;
	}
}