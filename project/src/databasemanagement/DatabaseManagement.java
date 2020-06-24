package databasemanagement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManagement {
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
	private static final String URL_PREFIX = "jdbc:mysql://";
	
	public DatabaseManagement(String url, String userName, String password, String databaseName) {
		this.databaseName = databaseName;
		this.url = URL_PREFIX + url;
		this.userName = userName;
		this.password = password;
		
		createDB();
	}
	
	public void createTable(String sqlCommand) {
		executeUpdate(sqlCommand);
	}
	
	public void deleteTable(String tableName) throws SQLException {
		String sql = DROP_TABLE + tableName;
		executeUpdate(sql);
		System.out.println("Table "+ tableName + " deleted");
	}
	
	public void createRow(String sqlCommand) {
		executeUpdate(sqlCommand);
	}

	public void createIOTEvent(String rawData) {
		String[] data = rawData.split(DELIMITER);
		executeUpdate(INSERT_IOTEVENT + data[0] + ",\"" + data[1] + "\"," + data[2] + ")");
	}
	
	public List<Object> readRow(String tableName, String primaryKeyColumnName, Object primaryKey) {
		List<Object> returnList = null;
		String sql = SELECT_FROM + tableName + WHERE + primaryKeyColumnName + EQUAL + primaryKey;
		
		try(Connection connection = DriverManager.getConnection(url + databaseName, userName, password);) {
			PreparedStatement pstmt = connection.prepareStatement(sql);
			returnList = resultSetToList(pstmt.executeQuery());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return returnList;
	}
	
	public Object readField(String tableName, String primaryKeyColumnName, Object primaryKey, int columnIndex) throws SQLException {
		List<Object> rowInList = readRow(tableName, primaryKeyColumnName , primaryKey);
		
		return rowInList.get(columnIndex - 1);
	}
	
	public Object readField(String tableName, String primaryKeyColumnName, Object primaryKey, String columnName) throws SQLException {
		try(Connection connection = DriverManager.getConnection(url + databaseName, userName, password);) {
			Statement statement = connection.createStatement();
			ResultSet res = statement.executeQuery(SELECT_FROM + tableName + WHERE + primaryKeyColumnName + EQUAL + primaryKey);
			if(res.next()) {
				return res.getObject(columnName);
			}
		}

		return null;
	}
	
	public void updateField(String tableName, String primaryKeyColumnName, Object primaryKey, int columnIndex, Object newValue) throws SQLException {
		try(Connection connection = DriverManager.getConnection(url + databaseName, userName, password);) {
			Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet res = statement.executeQuery(SELECT_FROM + tableName + WHERE + primaryKeyColumnName + EQUAL + primaryKey);
			if(res.next()) {
				res.updateObject(columnIndex, newValue);
				res.updateRow();
			}
		}
	}
	
	public void updateField(String tableName, String primaryKeyColumnName, Object primaryKey, String columnName, Object newValue) {
		String sql = UPDATE + tableName + SPACE + SET + columnName + EQUAL + newValue + WHERE + primaryKeyColumnName + EQUAL + primaryKey;
		executeUpdate(sql);
	}
	
	public void deleteRow(String tableName, String primaryKeyColumnName, Object primaryKey) throws SQLException {
        String sql = DELETE_FROM + tableName + WHERE + primaryKeyColumnName + EQUAL + primaryKey;
		executeUpdate(sql);
	}
	
	private void createDB() {
		try(Connection connection = DriverManager.getConnection(url, userName, password);) {			
			System.out.println("ctor connected to DB");
			Statement statement = connection.createStatement();
			statement.executeUpdate(CREATE_SCHEMA + databaseName);
			System.out.println("executed creat DB if not exists");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void executeUpdate(String sqlCommand) {
		try(Connection connection = DriverManager.getConnection(url + databaseName, userName, password);) {
			Statement statement = connection.createStatement();
			statement.executeUpdate(USE + databaseName);
			statement.executeUpdate(sqlCommand);
		} catch (SQLException e) {
			e.printStackTrace();
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
	
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException {		
		String url = "localhost:3306/";
		String databaseName = "iotExample";
		String user = "root";
		String password = "132435";
		
		DatabaseManagement company = new DatabaseManagement(url, user, password, databaseName);
		String sqlCommand = "CREATE TABLE IF NOT EXISTS IOTEvent (" + 
				"  `IOTEventID` INT NOT NULL AUTO_INCREMENT," + 
				"  `eventTypeID` INT NOT NULL," + 
				"  `timeStamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," + 
				"  `serialNumber` INT NOT NULL," + 
				"  PRIMARY KEY (`IOTEventID`))";
		company.createTable(sqlCommand);
		
		company.createRow("insert into IOTEvent values(1, 1, '2000-02-01 00:00:00', 1);");
		company.createRow("insert into IOTEvent values(2, 2, '2000-02-02 00:00:00', 2);");
		company.createRow("insert into IOTEvent values(3, 3, '2000-02-03 00:00:00', 3);");
		company.createRow("insert into IOTEvent values(4, 4, '2000-02-04 00:00:00', 4);");

		company.updateField("IOTEvent", "IOTEventID", 2, "eventTypeID", 5);
		company.updateField("IOTEvent", "IOTEventID", 4, 2, 8);

		//company.deleteRow("IOTEvent", "IOTEventID", 3);
		
		List<Object> rowlist = company.readRow("IOTEvent", "IOTEventID", 2);
		
		
		for(Object iterator : rowlist) {
			System.out.println(iterator);
		}

		
		System.out.println(company.readField("IOTEvent", "IOTEventID", 4, 2));
		
		//System.out.println(rowlist.toString());
		//company.deleteTable("IOTEvent");
		
		/*sqlCommand = "DROP TABLE IF EXISTS IOTEvent";
		company.deleteTable(sqlCommand);*/
	}

}