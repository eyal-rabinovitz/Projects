package databasemanagement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class erez {
	private Connection connection = null;
	private Statement statement = null;
	private final String databaseName;
	private final String url;
	private final String userName;
	private final String password;	
	private final String createDBString = "CREATE DATABASE IF NOT EXISTS ";	
//	private final String createTableString = "CREATE TABLE IF NOT EXISTS ";	-need to check if to use
	private final String alterTableString = "ALTER TABLE ";	
	private final String deleteTableString = "DROP TABLE ";	
//	private final String insertRowString = "INSERT INTO ";	-need to check if to use
	private final String IOTEventString = "IOTEvent(serialNumber, description, timeStamp)"	+  " VALUES(";
	private final String DeleteFromString = "DELETE FROM ";
	private final String selectAllString = "SELECT * FROM ";
	private final String useSrting = "USE ";
	private final String whereSrting = "WHERE ";
	
	public erez(String databaseName, 
							  String url, 
							  String userName, 
							  String password) 
							  throws ClassNotFoundException, SQLException {
		this.databaseName = databaseName;
		this.url = url;
		this.userName = userName;
		this.password = password;
		createDatabase(this.databaseName);
	}

	private void openConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
	    //System.out.println("Connecting to database...");
	    connection = DriverManager.getConnection(url, userName, password);
	    //System.out.println("Connected!");  
	  
	    statement = connection.createStatement();	
	}

	private void closeConnection() throws ClassNotFoundException, SQLException {
        if(null != statement) {
            statement.close();
        }
        if(null != connection) {
           connection.close();
        }
	}
	
	public void createDatabase(String NEWdatabaseName) 
							   throws ClassNotFoundException, SQLException {
    	openConnection();
	    statement.executeUpdate(createDBString + this.databaseName);
	    //System.out.println("Database created successfully...");	    
		closeConnection();	
	}
	
	public void createTable(String sqlCommand) throws ClassNotFoundException, SQLException {
    	openConnection();
    	executeSQLCommand(sqlCommand); //createTableString +  - need to check this
	    //System.out.println("Table created successfully...");	    
		closeConnection();
	}

	public void alterTable(String sqlCommand) throws ClassNotFoundException, SQLException {
    	openConnection();
    	executeSQLCommand(alterTableString + sqlCommand);
	    //System.out.println("Table altered successfully...");	    
		closeConnection();
	}	
	
	public void deleteTable(String tableName) throws SQLException, ClassNotFoundException {
    	openConnection();
    	executeSQLCommand(deleteTableString + tableName);
	    //System.out.println("Table" + tableName +"deleted successfully...");	    
		closeConnection();
	}
	
	public void createRow(String sqlCommand) throws ClassNotFoundException, SQLException {
    	openConnection();
    	executeSQLCommand(sqlCommand);//insertRowString +  - need to check this
	    //System.out.println("Row inserted successfully...");	    
		closeConnection();
	}

	public void createIOTEvent(String rawData) throws SQLException, ClassNotFoundException { 
		String[] rawDatasplit = rawData.split("\\|");
		String sqlCommand = IOTEventString;
		for (int i = 0; i < rawDatasplit.length; i++) {
			if(rawDatasplit.length - 1 == i) {
				sqlCommand += rawDatasplit[i] + ')';
			}
			else {
				sqlCommand += rawDatasplit[i] + ',';
			}
		}
		createRow(sqlCommand);
	}	
	
	public List<Object> readRow(String tableName,
								String primaryKeyColumnName, 
								Object value) 
								throws SQLException, ClassNotFoundException {
		openConnection();
		List<Object> ResultSetList = 
						convertResultSetToList(
								getSQLQueryResultSet(tableName, primaryKeyColumnName, value));
		closeConnection();
		
		return ResultSetList;
	}
	
	
	public Object readField(String tableName, 
							String primaryKeyColumnName, 
							Object value, 
							int columnIndex) 
							throws SQLException, ClassNotFoundException {
		openConnection();
		ResultSet resultSet = getSQLQueryResultSet(tableName, primaryKeyColumnName, value);
		Object resultSetObject = resultSet.getObject(columnIndex);
		closeConnection();
		
		return resultSetObject;
	}
	
	public Object readField(String tableName, 
							String primaryKeyColumnName, 
							Object value, 
							String columnName) 
							throws SQLException, ClassNotFoundException {
		openConnection();
		ResultSet resultSet = getSQLQueryResultSet(tableName, primaryKeyColumnName, value);
		Object resultSetObject = resultSet.getObject(columnName);
		closeConnection();
		
		return resultSetObject;
	}
	
	
	public void updateField(String tableName, 
							String primaryKeyColumnName,
							Object value, 
							int columnIndex, 
							Object newValue) 
							throws SQLException, ClassNotFoundException {
		openConnection();
		ResultSet resultSet = getSQLQueryResultSet(tableName, primaryKeyColumnName, value);
		resultSet.updateObject(columnIndex, newValue);
		resultSet.updateRow();
		closeConnection();
	}
	
	public void updateField(String tableName, 
							String primaryKeyColumnName, 
							Object value, 
							String columnName, 
							Object newValue) 
							throws SQLException, ClassNotFoundException {
		openConnection();
		ResultSet resultSet = getSQLQueryResultSet(tableName, primaryKeyColumnName, value);
		resultSet.updateObject(columnName, newValue);
		resultSet.updateRow();
		closeConnection();
	}
	
	public void deleteRow(String tableName,
						  String columnName, 
						  Object value) 
						  throws SQLException, ClassNotFoundException {
		executeSQLCommand(DeleteFromString +
						  tableName + whereSrting + columnName + "=" + value);
	}
	
/******************************************************************************/

	private ResultSet getSQLQueryResultSet(String tableName,
									 String primaryKeyColumnName, 
									 Object value) 
									 throws SQLException {
			statement.execute(useSrting + databaseName);
			String SQLQuery = selectAllString + tableName + 
							  whereSrting + primaryKeyColumnName + "=" + value;
			ResultSet resultSet = statement.executeQuery(SQLQuery);
			resultSet.next();
			
			return resultSet;
	}
	
	private List<Object> convertResultSetToList(ResultSet resultSet) throws SQLException {
		ResultSetMetaData metaData = resultSet.getMetaData();
	    int columnCount = metaData.getColumnCount();
	    List<Object> resultSetList = new ArrayList<>();
	    
	    for(int i = 1; i <= columnCount; ++i){
	    	resultSetList.add(resultSet.getObject(i));
	    }
	    
	    return resultSetList;
	}
	
	private void executeSQLCommand(String SQLCommand) throws SQLException, ClassNotFoundException {
		openConnection();
		statement.execute(useSrting + databaseName);
		statement.execute(SQLCommand);
		closeConnection();
	}
}