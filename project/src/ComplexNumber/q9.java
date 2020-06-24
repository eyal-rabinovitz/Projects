package ComplexNumber;

public class q9 {
	public static void main(String args[]) {
		System.out.println(findMin());
	}
	
	static int findMin(int... args) {
		if(args.length == 0) {
			throw new IllegalArgumentException("Too few arguments");
		}
		int min = args[0];
		for(int i = 0; i < args.length; ++i) {
			if(args[i] < min) {
				min = args[i];
			}
		}
		
		return min;
	}
}
