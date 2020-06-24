package executor;

import java.util.Random;
import java.util.concurrent.Callable;

class MyCallable implements Callable<Integer> { 
  
  public Integer call() throws Exception { 
    Random generator = new Random(); 
    Integer randomNumber = generator.nextInt(20); 
  
   // Thread.sleep(randomNumber * 1000); 
  
    return randomNumber; 
  } 
  
} 
