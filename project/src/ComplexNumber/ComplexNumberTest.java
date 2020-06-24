package ComplexNumber;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;


class ComplexNumberTest {

/*	@Test
	void testHashCode() {
	}

*/
	@Test
	void testAdd() {
		ComplexNumber num1 = new ComplexNumber(4, 5);
		ComplexNumber num2 = new ComplexNumber(2, 3);
		
		num1.add(num2);
		assertEquals(6, num1.getReal());	
		assertEquals(8, num1.getImaginary());	
	}

	@Test
	void testSubstract() {
		ComplexNumber num1 = new ComplexNumber(4, 5);
		ComplexNumber num2 = new ComplexNumber(2, 3);
		
		num1.substract(num2);
		assertEquals(2, num1.getReal());	
		assertEquals(2, num1.getImaginary());	
	}

	@Test
	void testMultiplyWith() {
		ComplexNumber num1 = new ComplexNumber(4, 5);
		ComplexNumber num2 = new ComplexNumber(2, 3);
		
		num1.multiplyWith(num2);
		assertEquals(-7, num1.getReal());	
		assertEquals(22, num1.getImaginary());
	}

	@Test
	void testDivideBy() {
		ComplexNumber num1 = new ComplexNumber(3, 2);
		ComplexNumber num2 = new ComplexNumber(4, -5);

		num1.divideBy(num2);

		assertEquals((double)2/41, num1.getReal());	
		assertEquals((double)23/41, num1.getImaginary());	
	}

	@Test
	void testIsRealIsImaginary() {
		ComplexNumber num =  new ComplexNumber(5,0);	
		assertEquals(true, num.isReal());	
		num.setValue(5, 10);
		assertEquals(false, num.isReal());	

		num.setValue(0, 10);
		assertEquals(true, num.isImaginary());
		assertEquals(false, num.isReal());
	}

	@Test
	void testParse() {
		ComplexNumber num = ComplexNumber.parse("2,i3");
		assertEquals("2.0 + 3.0i",num.toString());
	}

	@Test
	void testCompareTo() {
		ComplexNumber num1 =  new ComplexNumber(5,10);
		ComplexNumber num2 =  new ComplexNumber(5,10);
		assertEquals(0, num1.compareTo(num2));
		
		num1.setValue(0, 10);
		assertEquals(1, num1.compareTo(num2));
	}

	@Test
	void testEqualsObject() {
		ComplexNumber num1 =  new ComplexNumber(5,10);
		ComplexNumber num2 =  new ComplexNumber(5,10);
		assertEquals(true, num1.equals(num2));
		
		num1.setValue(0, 10);
		assertEquals(false, num1.equals(num2));
	}

	@Test
	void testToString() {
		ComplexNumber num =  new ComplexNumber(5,10);
		assertEquals("5.0 + 10.0i",num.toString());
		
		num.setValue(0, 10);
		assertEquals("10.0i",num.toString());
		
		num.setValue(5, 0);
		assertEquals("5.0",num.toString());

		num.setValue(5, -10);
		assertEquals("5.0 - 10.0i",num.toString());
		
	}

}
