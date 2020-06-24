package il.co.ilrd.httpiotserver;

import java.io.Serializable;
import java.util.List;

public class DatabaseManagementMessage implements Message<ActionTypeKey, List<Object>> , Serializable{
	private static final long serialVersionUID = 1L;
	private ActionTypeKey key;
	private List<Object> data;
	
	public DatabaseManagementMessage (ActionTypeKey key, List<Object> data) {
		this.key = key;
		this.data = data;
	}
	
	@Override
	public ActionTypeKey getKey() {
		return key;
	}
	
	@Override
	public List<Object> getData() {
		return data;
	}
	
	public void setKey(ActionTypeKey key) {
		this.key = key;
	}
	
	public void setData(List<Object> data) {
		this.data = data;
	}
	
	@Override
	public String toString() {
		return " " + data;
	}	
}