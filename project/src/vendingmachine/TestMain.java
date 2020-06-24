package vendingmachine;

import vendingmachine.VMProduct.Product;

public class TestMain {

	public static void main(String args[]) throws InterruptedException {
		VendingMachineState vm = new VendingMachineState();

		vm.startMachine();
		System.out.print("\n");
		
/*		vm.addMoney(10);
		vm.selectItem(Product.COCA_COLA);
		System.out.print("\n");
		*/

		vm.addMoney(10);
		Thread.sleep(5000);
		
		vm.selectItem(Product.COCA_COLA);
		vm.addMoney(10);
		Thread.sleep(5000);
		vm.addMoney(10);
		Thread.sleep(5000);
		vm.selectItem(Product.DIET_COLA);

		
		vm.turnOff();
		/*
		while(true) {
			System.out.print("round\n");
		}*/
/*		
		vm.addMoney(10);
		vm.selectItem(Product.DIET_COLA);
		System.out.print("\n");
		
		vm.addMoney(1);
		vm.selectItem(Product.COCA_COLA);	
		System.out.print("\n");

		System.out.print("\n");

		vm.addMoney(10);
		vm.selectItem(Product.COCA_COLA);	


		//vm.startMachine();
		System.out.print("\n");
		vm.startMachine();
		vm.addMoney(1);
		vm.addMoney(1);
		vm.addMoney(1);
		vm.selectItem(Product.SPRITE);	

		System.out.print("\n");
		vm.addMoney(10);
		vm.selectItem(Product.COCA_COLA);
		
		System.out.print("\n");
		vm.addMoney(10);
		vm.selectItem(Product.COCA_COLA);
	*/	
		
	}
}
