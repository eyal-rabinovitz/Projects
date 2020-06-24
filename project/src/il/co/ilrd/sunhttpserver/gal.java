//package il.co.ilrd.sunhttpserver;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.InetSocketAddress;
//import java.net.URI;
//import java.nio.ByteBuffer;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import org.json.JSONObject;
//import com.sun.net.httpserver.*;
//
//import il.co.ilrd.databasemanagement.*;
//import il.co.ilrd.http_message.HttpMethod;
//import il.co.ilrd.http_message.HttpStatusCode;
//
//public class gal {
//	private static final String PRI_KEY_COL_NAME = "primaryKeyName";
//	private static final String PRI_ROW_KEY = "primaryKeyValue";
//	private static final String WANTED_FIELD_COL_NAME = "columnName";
//	private static final String WANTED_FIELD_COL_INDEX = "columnIndex";
//	private static final String FIELD_VALUE = "fieldValue";
//	private static final String ROW_VALUES = "rowValues";	
//	private static final String CONTENT_TYPE = "Content-Type";
//	private static final String JSON_TYPE = "application/json";
//	private static final String ALLOW = "Allow";
//	private static final String OPTIONS_SUPPORTED = "[GET, POST, PUT, DELETE]";
//	private static final String SQL_COMMAND = "sqlCommand";
//	private static final String RAW_DATA = "rawData";
//	private static final String NEW_VALUE = "newValue";
//	private static final String EMPTY_BODY = "";
//	private static final String RESPONSE = "Response";
//	private static final String FIELD_INDEX = "/field_index/";
//	private static final String FIELD_NAME = "/field_name/";
//	private static final String IOT_EVENT = "/iotEvent/";
//	private static final String TABLE = "/table/";
//	private static final String ROW = "/row/";
//	private static final String ROOT = "/";
//
//	private Map<String, DatabaseManagement> companiesMap = new HashMap<>();
//	private HttpServer httpServer;
//	private String URL = "jdbc:mysql://localhost:3306/";
//	private String password = "305088155";
//	private String user = "root";
//	private RequestParser requestParser;
//	private DatabaseManagement companyDatabase;
//	
//	public gal(int portNumber) {
//		try {
//			httpServer = HttpServer.create(new InetSocketAddress(portNumber), 0);
//			addContexts();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public void start() {
//		httpServer.start();
//	}
//	
//	private void addContexts() {
//		httpServer.createContext(FIELD_INDEX, new FieldHandler());
//		httpServer.createContext(FIELD_NAME, new FieldHandler());
//		httpServer.createContext(IOT_EVENT, new IOTEventHandler());
//		httpServer.createContext(TABLE, new TableHandler());	
//		httpServer.createContext(ROW, new RowHandler());		
//		httpServer.createContext(ROOT, new RootHandler());
//	}
//	
//	private class FieldHandler implements HttpHandler {
//		private Map<HttpMethod, HttpMethodHandler> httpMethodHandlerMap = new HashMap<>();
//
//		private FieldHandler() {
//			httpMethodHandlerMap.put(HttpMethod.GET, readField);
//			httpMethodHandlerMap.put(HttpMethod.POST, updateField);
//			httpMethodHandlerMap.put(HttpMethod.OPTIONS, sendOptions);		
//		}
//
//		@Override
//		public void handle(HttpExchange httpExchange) throws IOException {
//			sendToDatabase(httpExchange, httpMethodHandlerMap);
//		}
//		
//		private HttpMethodHandler updateField = new HttpMethodHandler() {			
//			@Override
//			public void executeRequest(HttpExchange httpExchange, DatabaseManagement companyDatabase)
//					throws IOException, SQLException {
//				if(isFieldByName(getStringFromJson(WANTED_FIELD_COL_NAME))) {
//					companyDatabase.updateField(requestParser.getTableName(), getStringFromJson(PRI_KEY_COL_NAME), 
//												getObjectFromJson(PRI_ROW_KEY), getStringFromJson(WANTED_FIELD_COL_NAME), 
//												getStringFromJson(NEW_VALUE));
//					sendResponseMessage(httpExchange, HttpStatusCode.OK);		
//
//				} else if(isFieldByIndex(getStringFromJson(WANTED_FIELD_COL_INDEX))) {
//					companyDatabase.updateField(requestParser.getTableName(), getStringFromJson(PRI_KEY_COL_NAME),
//												getObjectFromJson(PRI_ROW_KEY), Integer.parseInt(getStringFromJson(WANTED_FIELD_COL_INDEX)), 
//												getStringFromJson(NEW_VALUE));
//					sendResponseMessage(httpExchange, HttpStatusCode.OK);
//					
//				} else {
//					sendResponseMessage(httpExchange, HttpStatusCode.BAD_REQUEST);
//				}
//			}
//		};
//		
//		private HttpMethodHandler readField = new HttpMethodHandler() {
//			@Override
//			public void executeRequest(HttpExchange httpExchange, DatabaseManagement companyDatabase)
//					throws IOException, SQLException {				
//				if(isFieldByName(getFromQueryString(WANTED_FIELD_COL_NAME))) {
//					Object resultValue = companyDatabase.readField(requestParser.getTableName(), 
//																getFromQueryString(PRI_KEY_COL_NAME), 
//																getFromQueryString(PRI_ROW_KEY), 
//																getFromQueryString(WANTED_FIELD_COL_NAME));
//					sendResponseMessage(httpExchange, HttpStatusCode.OK, convertResult(FIELD_VALUE, resultValue));	
//
//				} else if(isFieldByIndex(getFromQueryString(WANTED_FIELD_COL_INDEX))) {
//					Object resultValue = companyDatabase.readField(requestParser.getTableName(), 
//																getFromQueryString(PRI_KEY_COL_NAME), 
//																getFromQueryString(PRI_ROW_KEY), 
//																Integer.parseInt(getFromQueryString(WANTED_FIELD_COL_INDEX)));
//					sendResponseMessage(httpExchange, HttpStatusCode.OK, convertResult(FIELD_VALUE, resultValue));	
//				}
//			}			
//		};
//	}
//
//	private class IOTEventHandler implements HttpHandler {
//		private Map<HttpMethod, HttpMethodHandler> httpMethodHandlerMap = new HashMap<>();
//
//		private IOTEventHandler() {
//			httpMethodHandlerMap.put(HttpMethod.POST, createIotEvent);
//			httpMethodHandlerMap.put(HttpMethod.OPTIONS, sendOptions);		
//		}
//
//		@Override
//		public void handle(HttpExchange httpExchange) throws IOException {
//			sendToDatabase(httpExchange, httpMethodHandlerMap);
//		}
//		
//		private HttpMethodHandler createIotEvent = new HttpMethodHandler() {			
//			@Override
//			public void executeRequest(HttpExchange httpExchange, DatabaseManagement companyDatabase)
//					throws IOException, SQLException {				
//				companyDatabase.createIOTEvent(getStringFromJson(RAW_DATA));
//				sendResponseMessage(httpExchange, HttpStatusCode.CREATED);
//			}
//		};
//	}
//	
//	private class RootHandler implements HttpHandler {
//		private Map<HttpMethod, HttpMethodHandler> httpMethodHandlerMap = new HashMap<>();
//
//		private RootHandler() {
//			httpMethodHandlerMap.put(HttpMethod.OPTIONS, sendOptions);		
//		}
//
//		@Override
//		public void handle(HttpExchange httpExchange) {
//			try {
//				HttpMethodHandler handler = httpMethodHandlerMap.get(HttpMethod.valueOf(httpExchange.getRequestMethod()));
//				if(null != handler) {
//					handler.executeRequest(httpExchange, companyDatabase);					
//				} else {
//					sendResponseMessage(httpExchange, HttpStatusCode.NOT_IMPLEMENTED);
//				}
//				
//			} catch (IOException | SQLException e) {
//				e.printStackTrace();
//			} 
//		}		
//	}
//	
//	private HttpMethodHandler sendOptions = new HttpMethodHandler() {			
//		@Override
//		public void executeRequest(HttpExchange httpExchange, DatabaseManagement companyDatabase)
//				throws IOException, SQLException {				
//			sendOptionsMessage(httpExchange, HttpStatusCode.OK);
//		}
//	};	
//	
//	private class RowHandler implements HttpHandler {
//		private Map<HttpMethod, HttpMethodHandler> httpMethodHandlerMap = new HashMap<>();
//
//		private RowHandler() {
//			httpMethodHandlerMap.put(HttpMethod.GET, readRow);
//			httpMethodHandlerMap.put(HttpMethod.POST, createRow);
//			httpMethodHandlerMap.put(HttpMethod.DELETE, deleteRow);
//			httpMethodHandlerMap.put(HttpMethod.OPTIONS, sendOptions);
//		}
//
//		@Override
//		public void handle(HttpExchange httpExchange) throws IOException {
//			sendToDatabase(httpExchange, httpMethodHandlerMap);
//		}
//		
//		private HttpMethodHandler readRow = new HttpMethodHandler() {			
//			@Override
//			public void executeRequest(HttpExchange httpExchange, DatabaseManagement companyDatabase)
//					throws IOException, SQLException {				
//				List<Object> parameterList = companyDatabase.readRow(requestParser.getTableName(), 
//																	getFromQueryString(PRI_KEY_COL_NAME), 
//																	getFromQueryString(PRI_ROW_KEY));
//				sendResponseMessage(httpExchange, HttpStatusCode.OK, convertResult(ROW_VALUES, parameterList));
//			}
//		};
//		
//		private HttpMethodHandler createRow = new HttpMethodHandler() {			
//			@Override
//			public void executeRequest(HttpExchange httpExchange, DatabaseManagement companyDatabase)
//					throws IOException, SQLException {				
//				companyDatabase.createRow(getStringFromJson(SQL_COMMAND));
//				sendResponseMessage(httpExchange, HttpStatusCode.CREATED);
//			}
//		};
//		
//		private HttpMethodHandler deleteRow = new HttpMethodHandler() {			
//			@Override
//			public void executeRequest(HttpExchange httpExchange, DatabaseManagement companyDatabase)
//					throws IOException, SQLException {				
//				companyDatabase.deleteRow(requestParser.getTableName(), 
//										getFromQueryString(PRI_KEY_COL_NAME), 
//										getFromQueryString(PRI_ROW_KEY));
//				sendResponseMessage(httpExchange, HttpStatusCode.OK);
//			}
//		};
//	}
//	
//	private class TableHandler implements HttpHandler {
//		private Map<HttpMethod, HttpMethodHandler> httpMethodHandlerMap = new HashMap<>();
//
//		private TableHandler() {
//			httpMethodHandlerMap.put(HttpMethod.POST, createTable);
//			httpMethodHandlerMap.put(HttpMethod.DELETE, deleteTable);
//			httpMethodHandlerMap.put(HttpMethod.OPTIONS, sendOptions);
//		}
//		
//		@Override
//		public void handle(HttpExchange httpExchange) throws IOException{
//			sendToDatabase(httpExchange, httpMethodHandlerMap);						
//		}		
//
//		private HttpMethodHandler createTable = new HttpMethodHandler() {			
//			@Override
//			public void executeRequest(HttpExchange httpExchange, DatabaseManagement companyDatabase)
//					throws IOException, SQLException {
//				companyDatabase.createTable(getStringFromJson(SQL_COMMAND));
//				sendResponseMessage(httpExchange, HttpStatusCode.CREATED);
//			}
//		};
//		
//		private HttpMethodHandler deleteTable = new HttpMethodHandler() {			
//			@Override
//			public void executeRequest(HttpExchange httpExchange, DatabaseManagement companyDatabase)
//					throws IOException, SQLException {
//				companyDatabase.deleteTable(requestParser.getTableName());
//				sendResponseMessage(httpExchange, HttpStatusCode.OK);
//			}
//		};
//	}
//
//	private void sendToDatabase(HttpExchange httpExchange, Map<HttpMethod, HttpMethodHandler> httpMethodHandlerMap) throws IOException {
//		try {
//			requestParser = new RequestParser(httpExchange.getRequestURI(), httpExchange.getRequestBody());
//			companyDatabase = getCompanyFromMap(requestParser.getCompanyName());
//			HttpMethodHandler handler = httpMethodHandlerMap.get(HttpMethod.valueOf(httpExchange.getRequestMethod()));
//			if(null != handler) {
//				handler.executeRequest(httpExchange, companyDatabase);				
//			} else {
//				sendResponseMessage(httpExchange, HttpStatusCode.NOT_IMPLEMENTED);
//			}
//		} catch (Exception e) {
//			sendResponseMessage(httpExchange, HttpStatusCode.BAD_REQUEST, convertResult(RESPONSE, e));
//		}
//	}
//	
//	private DatabaseManagement getCompanyFromMap(String databaseName) throws SQLException {
//		companyDatabase = companiesMap.get(databaseName);			
//		if(null == companyDatabase) {
//			companyDatabase = new DatabaseManagement(URL, user, password, databaseName);
//			companiesMap.put(databaseName, companyDatabase);				
//		}
//		
//		return companyDatabase;
//	}
//	
//	private void sendOptionsMessage(HttpExchange httpExchange, HttpStatusCode statusCode) throws IOException {
//		Headers headers = httpExchange.getResponseHeaders();
//		headers.add(ALLOW, OPTIONS_SUPPORTED);
//		sendResponseMessage(httpExchange, statusCode, EMPTY_BODY, headers);
//	}
//	
//	private void sendResponseMessage(HttpExchange httpExchange, HttpStatusCode statusCode) throws IOException {
//		sendResponseMessage(httpExchange, statusCode, EMPTY_BODY);
//	}
//	
//	private void sendResponseMessage(HttpExchange httpExchange, HttpStatusCode statusCode, String body) throws IOException {
//		Headers headers = httpExchange.getResponseHeaders();
//		sendResponseMessage(httpExchange, statusCode, body, headers);
//	}
//	
//	private void sendResponseMessage(HttpExchange httpExchange, HttpStatusCode statusCode, String body, Headers headers) throws IOException {
//		headers.add(CONTENT_TYPE, JSON_TYPE);
//		httpExchange.sendResponseHeaders(statusCode.getCode(), body.length());
//		OutputStream outputStream = httpExchange.getResponseBody();
//		outputStream.write(body.getBytes());
//		outputStream.close();	
//	}
//	
//	private String convertResult(String key, Object resultValue) {
//		JSONObject jsonBody = new JSONObject();
//		jsonBody.put(key, resultValue);
//		return jsonBody.toString();
//	}
//	
//	private String getFromQueryString(String key) {
//		return requestParser.getQueryStringMap().get(key);
//	}
//
//	private String getStringFromJson(String key) {
//		return (String) requestParser.getJsonMap().get(key);
//	}
//	private Object getObjectFromJson(String key) {
//		return requestParser.getJsonMap().get(key);
//	}
//	
//	private boolean isFieldByIndex(String columnIndex) {
//		return (columnIndex != null);
//	}
//	
//	private boolean isFieldByName(String columnName) {
//		return (columnName != null);
//	}
//	
//	private class RequestParser {
//		private Map<String, Object> jsonMap = new HashMap<>();
//		private Map<String, String> queryStringMap = new HashMap<>();
//		private static final String FORWARD_SLASH = "[/]";
//		private static final String AMPERSAND = "[&]";
//		private static final String EQUAL = "[=]";
//		private static final int KEY_ARG = 0;
//		private static final int VALUE_ARG = 1;
//		private static final int COMPANY_NAME_ARG = 2;
//		private static final int TABLE_NAME_ARG = 3;
//		private static final int LIMIT = 2;
//		private String companyName;
//		private String tableName;
//		
//		public RequestParser(URI uri, InputStream inputStream) throws IOException {
//			parsePath(uri.getPath());
//			parseQueryString(uri.getQuery());
//			parseBody(inputStream);						
//		}				
//
//		private void parseBody(InputStream inputStream) throws IOException {
//			if(inputStream.available() > 0) {
//				byte[] bodyBytes = new byte[2048];
//				while(-1 != inputStream.read(bodyBytes));
//				JSONObject jsonBody = new JSONObject(new String(bodyBytes));			
//				jsonMap = jsonBody.toMap();					
//			}
//		}
//
//		private void parsePath(String path) {
//			String[] requestTokens = path.split(FORWARD_SLASH);			
//			companyName = requestTokens[COMPANY_NAME_ARG];
//			tableName = requestTokens[TABLE_NAME_ARG];
//		}
//
//		private void parseQueryString(String query) {
//			if(null != query) {
//				String[] parameters = query.split(AMPERSAND);	
//				String[] row;
//				for(String parameter : parameters) {
//					row = parameter.split(EQUAL, LIMIT);
//					queryStringMap.put(row[KEY_ARG], row[VALUE_ARG]);
//				}					
//			}
//		}
//
//		private Map<String, Object> getJsonMap() {
//			return jsonMap;
//		}
//
//		private Map<String, String> getQueryStringMap() {
//			return queryStringMap;
//		}
//
//		private String getCompanyName() {
//			return companyName;
//		}
//
//		private String getTableName() {
//			return tableName;
//		}
//	}
//}
//
//interface HttpMethodHandler {
//	public abstract void executeRequest(HttpExchange httpExchange, DatabaseManagement companyDatabase) 
//			throws IOException, SQLException;
//}