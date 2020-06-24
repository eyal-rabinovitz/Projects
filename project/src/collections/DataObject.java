package collections;

public class DataObject {
	String code;
	int value;
	
	public DataObject(String code, int value) {
		this.code = code;
		this.value = value;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
