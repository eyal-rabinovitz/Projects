package vendingmachine;

import java.util.concurrent.atomic.AtomicBoolean;

import vendingmachine.VMProduct.Product;

public class VendingMachineState {
	static final int END_TIME = 10;
	protected int timer = 0;
	private AtomicBoolean isOn = new AtomicBoolean(true);
	private double currentMoney = 0;
	private States currentState = States.INIT;
	private Monitor monitor = new TestMonitor();
	private VMProduct[] products = new VMProduct[5];
	static Thread thread;

	{
		products[0] = new VMProduct(VMProduct.Product.COCA_COLA, 2, 4.5);
		products[1] = new VMProduct(VMProduct.Product.DIET_COLA, 10, 5.5);
		products[2] = new VMProduct( VMProduct.Product.SPRITE, 5,3.0);
		products[3] = new VMProduct(VMProduct.Product.FANTA, 8, 3.9);
		products[4] = new VMProduct(VMProduct.Product.FUSE_TEA, 15, 4.0);		
	}
	
	public enum States {
		INIT {
			@Override
			public void start(VendingMachineState vm) {				
				vm.monitor.print("machine is ready \n");
				vm.currentState = WAIT_FOR_MONEY;
				vm.isOn.set(true);
				Runnable myRunnable = new Runnable() {
					public void run() {
						while(vm.isOn.get()) {
							try {
			    				Thread.sleep(1000);
			    			} catch (InterruptedException e) {
			    				e.printStackTrace();
			    			}
							vm.currentState.timeOut(vm);
						}
					}
				};
				thread = new Thread(myRunnable);
			    thread.start();
			}	
			
			@Override
			public void insertMoney(VendingMachineState vm, double money) {
				vm.monitor.print("start machine first \n");
			}
			
			@Override
			public void selectProduct(VendingMachineState vm, Product product) {
				vm.monitor.print("start machine first \n");
			}
			
			@Override
			public void askForRefund(VendingMachineState vm) {
				vm.monitor.print("start machine first \n");
			}
		},
		WAIT_FOR_MONEY {
			@Override
			public void selectProduct(VendingMachineState vm, Product product) {
				vm.monitor.print("the price for this product is = " + vm.products[product.getId()].getPrice() + " \n");
			}
			
			@Override
			public void askForRefund(VendingMachineState vm) {
				vm.monitor.print("you need to insert first \n");
			}
		},
		WAIT_FOR_CHOOSING {
			@Override
			public void start(VendingMachineState vm) {
				this.askForRefund(vm);
				vm.currentState = WAIT_FOR_MONEY;
			}	
			
			@Override
			public void selectProduct(VendingMachineState vm, Product product) {
				int productIndex = product.getId();
				double productPrice = vm.products[productIndex].getPrice();
				if(vm.currentMoney < productPrice) {
					vm.monitor.print("not enough money \n");
				}
				else if(vm.products[productIndex].getAmount() <= 0) {
					vm.monitor.print("not available \n");
				}
				else {
					vm.currentMoney -= productPrice;
					vm.monitor.print("take your product " + product + "\n");
					vm.products[productIndex].decreaseAmount();
				}
				
				askForRefund(vm);
			}
			
			@Override
			public void askForRefund(VendingMachineState vm) {
				vm.monitor.print("take your money = " + vm.currentMoney + "\n");
				vm.currentMoney = 0;
				vm.currentState = WAIT_FOR_MONEY;
				vm.timer = 0;
			}
			
			@Override
			public void timeOut(VendingMachineState vm) {
				if (END_TIME <= vm.timer) {
					vm.monitor.print("time out in wait for choosing \n");
					vm.currentState.askForRefund(vm);
					vm.currentState = WAIT_FOR_MONEY;
				}
				
				++vm.timer;
			}
		};
		
		public abstract void selectProduct(VendingMachineState vm, Product product);
		public abstract void askForRefund(VendingMachineState vm);
		
		public void insertMoney(VendingMachineState vm, double money) {
			vm.currentMoney += money;
			vm.timer = 0;
			vm.currentState = WAIT_FOR_CHOOSING;
		}
		
		public void start(VendingMachineState vm) {
			vm.monitor.print("machine is ready \n");
		}
		
		public void turnOff(VendingMachineState vm) {
			this.askForRefund(vm);
			vm.monitor.print("machine is off \n");
			vm.currentState = INIT;
			vm.isOn.set(false);
		}	

		public void timeOut(VendingMachineState vm) {
			vm.timer = 0;
		}
	}
	
	public void startMachine() {
		currentState.start(this);
	}
	public void addMoney(double money) {
		currentState.insertMoney(this, money);
	}
	
	public void selectItem(Product product) {
		currentState.selectProduct(this, product);
	}
	
	public void getRefund() {
		currentState.askForRefund(this);
	}
	
	public void turnOff() {
		currentState.turnOff(this);
	}
}
