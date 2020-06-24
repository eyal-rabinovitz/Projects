package inner_class;

public class DataStructure {
   
   private final static int SIZE = 15;
   private int[] arrayOfInts = new int[SIZE];
   
   public DataStructure() {
       for (int i = 0; i < SIZE; i++) {
           arrayOfInts[i] = i;
       }
   }
   
   public void printEven() {
       DataStructureIterator iterator = this.new EvenIterator();
       while (iterator.hasNext()) {
           System.out.print(iterator.next() + " ");
       }
       System.out.println();
   }

   private int getSize() {
	   return SIZE;
   }
   
   public void anonymous() {
	DataStructure anonym = new DataStructure() {
		   public void print() {
			   System.out.println(" ");
		   }
		};	
   }
   
   private class EvenIterator implements DataStructureIterator {
       private int nextIndex = 0;
       
       public boolean hasNext() {
    	   return (nextIndex <= SIZE - 1);
       }        
       
       public Integer next() {
           Integer retValue = Integer.valueOf(arrayOfInts[nextIndex]);
           nextIndex += 2;
           
          // System.out.print("inner class invoke method from Outer" + DataStructure.this.getSize() + "\n");
          // System.out.print("inner class access variable from outer" + DataStructure.this.SIZE + "\n");

           
           return retValue;
       }
       
       public void invokeFromOuter() {
           System.out.print("inner class " + DataStructure.this.getSize());
       }
   }
   
   public static void main(String s[]) {

      DataStructure ds = new DataStructure();
      ds.printEven();
       
       
   }
}