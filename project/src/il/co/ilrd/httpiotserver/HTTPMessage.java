package il.co.ilrd.httpiotserver;

import java.io.Serializable;


public class HTTPMessage implements Message<String, String> , Serializable{
	private static final long serialVersionUID = 1L;
	private String key;
	private String data;
	
	public HTTPMessage(String key, String data) {
		this.key = key;
		this.data = data;
	}
	@Override
	public String getKey() {
		return key;
	}
	@Override
	public String getData() {
		return data;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public void setData(String data) {
		this.data = data;
	}	
}