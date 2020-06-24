package vendingmachine;

public class VMProduct {

	private double price;
	private int amount;
	Product product;
	
	public VMProduct(Product product, int amount, double price) {
		this.amount = amount;
		this.product = product;
		this.price = price;
		
	}
	
	public int getAmount() {
		return amount;
	}
	
	public void setAmount(int amount) {
		this.amount = amount;
	}	
	
	public void decreaseAmount() {
		--amount;
	}	
	
	public double getPrice() {
		return price;
	}

	public enum Product {
		
		COCA_COLA (0),
		DIET_COLA (1),
		SPRITE    (2),
		FANTA     (3),
		FUSE_TEA  (4);
		
		private int id;
		
		private Product (int id) {
			this.id = id;
		}
		
		public int getId() {
			return id;
		}

	}
}