package ComplexNumber;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ComplexNumber implements Comparable<ComplexNumber> {
	private double real;
	private double imaginary;

	public ComplexNumber() {
		this.real = 0;
		this.imaginary = 0;
	}

	public ComplexNumber(double real, double imaginary) {
		this.real = real;
		this.imaginary = imaginary;
	}

	public double getReal() {
		return real;
	}

	public void setReal(double real) {
		this.real = real;
	}

	public double getImaginary() {
		return imaginary;
	}

	public void setImaginary(double imaginary) {
		this.imaginary = imaginary;
	}
	
	public void setValue(double real, double imaginary) {
		this.real = real;
		this.imaginary = imaginary;
	}
	
	public ComplexNumber add(ComplexNumber num) {
		this.real += num.real;
		this.imaginary += num.imaginary;	
		
		return this;
	}
	
	public ComplexNumber substract(ComplexNumber num) {
		this.real -= num.real;
		this.imaginary -= num.imaginary;
		
		return this;
	}
	
	public ComplexNumber multiplyWith(ComplexNumber num) {
		double originalReal = this.real;
		this.real = ((this.real * num.real) - (this.imaginary * num.imaginary));
		this.imaginary = (this.imaginary * num.real) + (originalReal * num.imaginary);
		
		return this;
	}
	
	public ComplexNumber divideBy(ComplexNumber num) {
		if((0 == num.real) && (0 == num.imaginary)) {
			return null;
		}
		
		ComplexNumber conjugate = new ComplexNumber(num.real, -num.imaginary);
		ComplexNumber numCopy = new ComplexNumber(num.real, num.imaginary);

		this.multiplyWith(conjugate);
		numCopy.multiplyWith(conjugate);

		this.real /= numCopy.real;
		this.imaginary /= numCopy.real;
		
		return this;
	}

	public boolean isReal() {
		return (0 == this.imaginary);
	}
	
	public boolean isImaginary() {
		return ((0 != this.imaginary) && (0 == this.real));
	}
	
	public static ComplexNumber parse(String strComplexNum) {
        boolean isMatch = Pattern.matches
        				("(\\s*\\-?\\d+(\\.\\d+)?)([,\\/]|[*\\s])(\\-?i\\d+(\\.\\d+)?)\\s*", strComplexNum);
        
        if(false == isMatch) {
        	return null;
        }
        
        String[] arrOfStr = strComplexNum.split(",|\\s"); 
		ComplexNumber newNum = new ComplexNumber(Double.parseDouble(arrOfStr[0]), 
												 Double.parseDouble(arrOfStr[1].replaceAll("[^\\d.-]", "")));

		return newNum;
	}

	@Override
	public int compareTo(ComplexNumber num) {
		return this.equals(num) ? 0 : 1;
	}

	@Override
	public boolean equals(Object obj) {
		if(null == obj) {
			return false;
		}

		if(!(obj instanceof ComplexNumber)) {
			return false;
		}
		
		ComplexNumber num = (ComplexNumber)obj;
		return (this.real == num.real && this.imaginary == num.imaginary); 
	}

	@Override
	public int hashCode() {
		return Objects.hash(real, imaginary);
	}

	@Override
	public String toString() {
		String realString = (0 != this.real) ? "" + this.real : "";
		String imaginaryString = (0 != this.imaginary) ? Math.abs(this.imaginary) + "i" : "";
		String operatorString = (this.isReal() || this.isImaginary()) ? "" : " + ";
		if(0 > this.imaginary) {
			operatorString = " - ";
		}
		
		return realString + operatorString + imaginaryString;
	}
}
