package databasemanagement;

public class ActionTypeKey {
	private String databaseName;
	private DatabaseKeys actionType;
	
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
