package il.co.ilrd.chatserver;

import java.io.Serializable;

public class PingPongServerMessage implements Message<String, Void> , Serializable{
	private static final long serialVersionUID = 1L;
	private String key;
	private Void data;
	
	public PingPongServerMessage (String key) {
		this.key = key;
		this.data = null;
	}
	
	@Override
	public String getKey() {
		return key;
	}
	
	@Override
	public Void getData() {
		return data;
	}
	
	@Override
	public String toString() {
		return "key is " + key;
	}	
}