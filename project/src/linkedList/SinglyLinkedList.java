package linkedList;

public class SinglyLinkedList {
	private Node head = new Node(null, null);
	
		private class Node {
			private Node nodeNext;
			private Object data;
			
			private Node(Object data, Node nodeNext) { 
				this.data = data;
				this.nodeNext = nodeNext;
			}
			
			private Node getNodeNext() {
				return this.nodeNext;
			}
						
			private Object getData() {
				return this.data;
			}
		}
	
		private class IterratorIMP implements Iterator {
			
			private Node current = head; 
			
			@Override
			public boolean hasNext() {
				return (null != current.getNodeNext());
			}
			
			@Override
			public Object next() {
				Object returnData = current.getData();
				current = current.getNodeNext();
				
				return returnData;
			}	
		}	
		
	public void pushFront(Object data) {
		Node newNode = new Node(data, head);
		head = newNode;
	}
	
	public Object popFront() {
		if(this.isEmpty()) {
			return null;
		}
		
		Node nodeToReturn = head;
		head = head.getNodeNext();
		
		return nodeToReturn;
	}
	
	public int size() {
		int counter = 0;
		Node runner = head;
		while(null != runner.getNodeNext()) {			
			++counter;
			runner = runner.getNodeNext();
		}
		
		return counter;
	}
	
	public boolean isEmpty() {
		return (head.getNodeNext() == null);
	}
	
	public Iterator begin() {
		return new IterratorIMP();
	}
	
	public Iterator find(Object data) {
		Iterator curr = begin();
		Iterator prev = begin();

		while(curr.hasNext()) {
			if(curr.next().equals(data)) {
				return prev;
			}
			prev.next();
		}
		return null;	
	}
}

