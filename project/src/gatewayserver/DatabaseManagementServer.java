package gatewayserver;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManagementServer implements DatabaseManagementInterface {
	private final String databaseName;
	private final String url;
	private final String userName;
	private final String password;
	private static final String CREATE_SCHEMA = "CREATE SCHEMA IF NOT EXISTS ";
	private static final String DROP_TABLE = "DROP TABLE ";
	private static final String SELECT_FROM = "SELECT * FROM ";
	private static final String DELETE_FROM = "DELETE FROM ";
	private static final String DELIMITER = "\\|";
	private static final String WHERE = " WHERE ";
	private static final String UPDATE = "UPDATE ";
	private static final String USE = "USE ";
	private static final String EQUAL = " = ";
	private static final String SET = "SET ";
	private static final String SPACE = " ";
	private static final String INSERT_IOTEVENT = "INSERT INTO IOTEvent VALUES (null,";
	
	public DatabaseManagementServer(String url, String userName, String password, String databaseName) throws SQLException {
		this.databaseName = databaseName;
		this.url = url;
		this.userName = userName;
		this.password = password;
		
		createDB();
	}

	public void createTable(String sqlCommand) throws SQLException {
		executeUpdate(sqlCommand);
	}
	
	public void deleteTable(String tableName) throws SQLException {
		String sql = DROP_TABLE + tableName;
		executeUpdate(sql);
		System.out.println("Table "+ tableName + " deleted");
	}
	
	public void createRow(String sqlCommand) throws SQLException {
		executeUpdate(sqlCommand);
	}

	public void createIOTEvent(String rawData) throws SQLException {
		String[] data = rawData.split(DELIMITER);
		executeUpdate(INSERT_IOTEVENT + data[0] + ",\"" + data[1] + "\"," + data[2] + ")");
	}
	
	public List<Object> readRow(String tableName, String primaryKeyColumnName, Object primaryKey) throws SQLException {
		List<Object> returnList = null;
		String sql = SELECT_FROM + tableName + WHERE + primaryKeyColumnName + EQUAL + primaryKey;
		
		try(Connection connection = DriverManager.getConnection(url, userName, password);) {
			Statement statement = connection.createStatement();
			statement.executeQuery(USE + databaseName + ";");
			
			returnList = resultSetToList(statement.executeQuery(sql));
		}

		return returnList;
	}
	
	public Object readField(String tableName, String primaryKeyColumnName, Object primaryKey, int columnIndex) throws SQLException {
		List<Object> rowInList = readRow(tableName, primaryKeyColumnName , primaryKey);
		
		return rowInList.get(columnIndex - 1);
	}
	
	public Object readField(String tableName, String primaryKeyColumnName, Object primaryKey, String columnName) throws SQLException {
		try(Connection connection = DriverManager.getConnection(url, userName, password);) {
			Statement statement = connection.createStatement();
			statement.executeQuery(USE + databaseName + ";");
			ResultSet res = statement.executeQuery(SELECT_FROM + tableName + WHERE + primaryKeyColumnName + EQUAL + primaryKey);
			if(res.next()) {
				return res.getObject(columnName);
			}
		}

		return null;
	}
	
	public void updateField(String tableName, String primaryKeyColumnName, Object primaryKey, int columnIndex, Object newValue) throws SQLException {
		try(Connection connection = DriverManager.getConnection(url, userName, password);) {
			Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			statement.executeQuery(USE + databaseName + ";");
			ResultSet res = statement.executeQuery(SELECT_FROM + tableName + WHERE + primaryKeyColumnName + EQUAL + primaryKey);
			if(res.next()) {
				res.updateObject(columnIndex, newValue);
				res.updateRow();
			}
		}
	}
	
	public void updateField(String tableName, String primaryKeyColumnName, Object primaryKey, String columnName, Object newValue) throws SQLException {
		String sql = UPDATE + tableName + SPACE + SET + columnName + EQUAL + newValue + WHERE + primaryKeyColumnName + EQUAL + primaryKey;
		executeUpdate(sql);
	}
	
	public void deleteRow(String tableName, String primaryKeyColumnName, Object primaryKey) throws SQLException {
        String sql = DELETE_FROM + tableName + WHERE + primaryKeyColumnName + EQUAL + primaryKey;
		executeUpdate(sql);
	}
	
	private void createDB() throws SQLException {
		try(Connection connection = DriverManager.getConnection(url, userName, password);) {			
			System.out.println("ctor connected to DB");
			Statement statement = connection.createStatement();
			statement.executeUpdate(CREATE_SCHEMA + databaseName);
			System.out.println("executed creat DB if not exists");
		}
	}
	
	private void executeUpdate(String sqlCommand) throws SQLException {
		try(Connection connection = DriverManager.getConnection(url, userName, password);) {
			Statement statement = connection.createStatement();
			statement.executeUpdate(USE + databaseName);
			statement.executeUpdate(sqlCommand);
		}
	}
	
	private List<Object> resultSetToList(ResultSet res) throws SQLException {
		List<Object> returnList = new ArrayList<>();
		int numOfColumns = res.getMetaData().getColumnCount();

		while(res.next()) {
			for(int i = 1 ;i <= numOfColumns; ++i) {
				returnList.add(res.getObject(i));
			}			
		}
		
		return returnList;
	}
	
	/*jdbc worksheet*/
	public void findIsolation() throws SQLException {
		try(Connection connection = DriverManager.getConnection(url, userName, password);) {
			Statement statement = connection.createStatement();
			statement.executeQuery(USE + databaseName + ";");
			DatabaseMetaData dbMetaData = connection.getMetaData();
			
			if (dbMetaData.supportsTransactionIsolationLevel(Connection.TRANSACTION_SERIALIZABLE)) {
				System.out.println("Transaction Isolation level= " + connection.getTransactionIsolation());
				
				// Setting Transaction Isolation Level, can set Its String Value or its int value
				//connection.setTransactionIsolation(2);
			}
			else if (dbMetaData.supportsTransactionIsolationLevel(Connection.TRANSACTION_READ_COMMITTED)) {
				System.out.println("Transaction Isolation level= " + connection.getTransactionIsolation());				
			}
		}

	}
	
}