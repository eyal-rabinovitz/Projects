package linkedList;

public class Main {

	public static void main(String[] args) /*throws ClassNotFoundException */{
	
		SinglyLinkedList list = new SinglyLinkedList();
		Iterator iter = list.begin();
		System.out.println("should be false");
		System.out.println(iter.hasNext());
		
		list.pushFront(10);
		list.pushFront(20);
		list.pushFront(30);
		list.pushFront(40);

		iter = list.begin();

		System.out.println("should be 40");
		System.out.println(iter.next());
		System.out.println("should be true");
		System.out.println(iter.hasNext());

		System.out.println("should be 30");
		System.out.println(iter.next());

		System.out.println("should be 20");
		System.out.println(iter.next());
		
		System.out.println("should be 10");
		System.out.println(iter.next());

		
		System.out.println("size should be 4");
		System.out.println(list.size());

		list.popFront();
		System.out.println("size should be 3");
		System.out.println(list.size());	
		
		list.popFront();
		System.out.println("size should be 2");
		System.out.println(list.size());
		
		list.popFront();
		System.out.println("size should be 1");
		System.out.println(list.size());
		
		list.popFront();
		System.out.println("size should be 0");
		System.out.println(list.size());
		System.out.println("should be false");
		System.out.println(iter.hasNext());
		
		list.popFront();
		System.out.println("size should be still 0");
		System.out.println(list.size());
		

		list.pushFront(10);
		list.pushFront(20);
		list.pushFront(30);
		list.pushFront(40);
		
		Iterator iter_find = list.find(20); /*fix find*/
		System.out.println("should be 20");
		System.out.println(iter_find.next());

		iter_find = list.find(40);
		System.out.println("should be 40");
		System.out.println(iter_find.next());
	
		iter_find = list.find(10);
		System.out.println("should be null");
		if(iter_find.next() == null) {
			System.out.println("null");
		}
	
	}

}
/*iter = list.begin();
		if((int)(iter.next()) == 40) {
			 System.out.println("success");
		}
		else {
			 System.out.println("fail");
		}
		if((int)(iter.next()) == 30) {
			 System.out.println("success");
		}
		else {
			 System.out.println("fail");
		}
		if((int)(iter.next()) == 20) {
			 System.out.println("success");
		}
		else {
			 System.out.println("fail");
		}
 */