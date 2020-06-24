package il.co.ilrd.sunhttpserver;

import java.io.Serializable;

public class ActionTypeKey implements Serializable {
	private static final long serialVersionUID = 1L;
	private String databaseName;
	private DatabaseKeys actionType;
	
	public ActionTypeKey(String databaseName, DatabaseKeys actionType) {
		this.databaseName = databaseName;
		this.actionType = actionType;
	}
	
	public String getDatabaseName() {
		return databaseName;
	}
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	public DatabaseKeys getActionType() {
		return actionType;
	}
	public void setActionType(DatabaseKeys actionType) {
		this.actionType = actionType;
	}
}
