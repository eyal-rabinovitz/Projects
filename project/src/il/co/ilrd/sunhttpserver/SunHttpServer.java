package il.co.ilrd.sunhttpserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import http_message.HttpMethod;
import http_message.HttpStatusCode;

public class SunHttpServer {
	private Map<String, DatabaseManagementServer> companiesMap = new HashMap<>();
	private HttpServer httpServer;
	private Headers headers;
	private static final String URL = "jdbc:mysql://localhost/";
	private static final String USER_NAME = "root";
	private static final String PASSWORD = "132435";
	private final static String PRIMARY_KEY_NAME = "primaryKeyName";
	private final static String PRIMARY_KEY_VALUE = "primaryKeyValue";
	private final static String COLUMN_NAME = "columnName";
	private final static String COLUMN_INDEX = "columnIndex";
	private final static String TABLE_NAME = "tableName";
	private final static String NEW_VALUE = "newValue";
	private final static String SQL_COMMAND = "sqlCommand";
	private static final String ERROR = "Error";

	public SunHttpServer(int portNumber) throws IOException {
		httpServer = HttpServer.create(new InetSocketAddress(portNumber), 0);
		addContexts();
	}

	private void addContexts() {
		httpServer.createContext("/table/", new TableHandler());
		httpServer.createContext("/row/", new RowHandler());
		httpServer.createContext("/field_name/", new FieldNameHandler());
		httpServer.createContext("/field_index/", new FieldIndexHandler());
		httpServer.createContext("/iotEvent/", new IOTEventHandler());
		httpServer.createContext("/", new RootHandler());
	}
	
	public void start() {
		httpServer.start();
	}
	
	public void stop(int delay) {
		httpServer.stop(delay);
	}

	private void sendMessage(HttpExchange httpExchange, HttpStatusCode statusCode, String message) throws IOException {
		headers = httpExchange.getResponseHeaders();
		fillHeadersWithStandardHeaders(headers);
		httpExchange.sendResponseHeaders(statusCode.getCode(), message.length());
		OutputStream os = httpExchange.getResponseBody();
		os.write(message.getBytes());
		os.close();
	}

	private void fillHeadersWithStandardHeaders(Headers headers) {
		headers.add("Content-Type", "application/json");
		headers.add("Connection", "closed");
	}


	private void createDBIfNotExist(String dbName) throws SQLException {
		if(!companiesMap.containsKey(dbName)) {
			companiesMap.put(dbName, new DatabaseManagementServer(URL, USER_NAME, PASSWORD, dbName));
		}
	}
	
	private JSONObject getBodyAsJSONObject(HttpExchange httpExchange) throws IOException, JSONException {
		byte[] byteArray = new byte[4096];
		InputStream is = httpExchange.getRequestBody();
		is.read(byteArray);

		return new JSONObject(new String(byteArray).trim());
	}
	
	private String responseBodyBuilder(String name, String value) {
		return ("{ \""+ name + "\":  \"" + value + "\" }");
	}
	
	public void httpExchangeHandle(HttpExchange httpExchange, Map<HttpMethod, HttpMethodFunction<HttpExchange, RequestParser>> dbMethodMap) throws IOException {
		try {
			System.out.println(httpExchange.getRequestURI().getQuery());

			RequestParser requestParser = new RequestParser(httpExchange.getRequestURI(), httpExchange.getRequestBody());
			createDBIfNotExist(requestParser.getCompanyName());
			if(null != dbMethodMap.get(HttpMethod.valueOf(httpExchange.getRequestMethod()))) {
				dbMethodMap.get(HttpMethod.valueOf(httpExchange.getRequestMethod())).apply(httpExchange, requestParser);				
			}
			else {
				sendMessage(httpExchange, HttpStatusCode.BAD_REQUEST, ERROR);	
			}
		} catch(SQLException | ClassCastException | IndexOutOfBoundsException | IOException | JSONException e) {
			sendMessage(httpExchange, HttpStatusCode.BAD_REQUEST, e.getMessage());	
		}
	}
	
	private class TableHandler implements HttpHandler {
		private Map<HttpMethod, HttpMethodFunction<HttpExchange, RequestParser>> dbMethodMap = new HashMap<>();
		
		private TableHandler() {
			dbMethodMap.put(HttpMethod.POST, new CreateTableHandler());
			dbMethodMap.put(HttpMethod.DELETE, new DeleteTableHandler());
		}

		@Override
		public void handle(HttpExchange httpexchange) throws IOException {
			httpExchangeHandle(httpexchange, dbMethodMap);
		}
		
		private class CreateTableHandler implements HttpMethodFunction<HttpExchange, RequestParser> {
			private static final String ACK = "Table created";

			@Override
			public void apply(HttpExchange httpExchange, RequestParser requestParser) throws SQLException, IOException, JSONException {
				JSONObject json = getBodyAsJSONObject(httpExchange);
				System.out.println(json.getString(SQL_COMMAND));
				companiesMap.get(requestParser.getCompanyName()).createTable(json.getString(SQL_COMMAND));
				
				sendMessage(httpExchange, HttpStatusCode.OK, ACK); 
			}
		}
		
		private class DeleteTableHandler implements HttpMethodFunction<HttpExchange, RequestParser> {
			private static final String ACK = "Table deleted";
			
			@Override
			public void apply(HttpExchange httpExchange, RequestParser requestParser) throws SQLException, IOException {
				companiesMap.get(requestParser.getCompanyName()).deleteTable(requestParser.getTableName());

				sendMessage(httpExchange, HttpStatusCode.OK, ACK); 
			}
		}
	}
	
	private class RowHandler implements HttpHandler {
		private Map<HttpMethod, HttpMethodFunction<HttpExchange, RequestParser>> dbMethodMap = new HashMap<>();
		
		private RowHandler() {
			dbMethodMap.put(HttpMethod.POST, new CreateRowHandler());
			dbMethodMap.put(HttpMethod.DELETE, new DeleteRowHandler());
			dbMethodMap.put(HttpMethod.GET, new ReadRowHandler());
		}

		@Override
		public void handle(HttpExchange httpexchange) throws IOException {
			httpExchangeHandle(httpexchange, dbMethodMap);
		}
		
		private class CreateRowHandler implements HttpMethodFunction<HttpExchange, RequestParser> {
			private static final String ACK = "Row created";
			
			@Override
			public void apply(HttpExchange httpExchange, RequestParser requestParser) throws SQLException, IOException, JSONException {
				JSONObject json = getBodyAsJSONObject(httpExchange);

				companiesMap.get(requestParser.getCompanyName()).createRow(json.getString(SQL_COMMAND));

				sendMessage(httpExchange, HttpStatusCode.CREATED, ACK); 
			}
		}
		
		private class DeleteRowHandler implements HttpMethodFunction<HttpExchange, RequestParser> {
			private static final String ACK = "Row deleted";

			@Override
			public void apply(HttpExchange httpExchange, RequestParser requestParser) throws SQLException, JSONException, IOException {
				JSONObject json = getBodyAsJSONObject(httpExchange);
				companiesMap.get(requestParser.getCompanyName()).deleteRow(json.getString(TABLE_NAME),
																		json.getString(PRIMARY_KEY_NAME),
																		json.get(PRIMARY_KEY_VALUE));

				sendMessage(httpExchange, HttpStatusCode.OK, ACK); 
			}
		}
		
		private class ReadRowHandler implements HttpMethodFunction<HttpExchange, RequestParser> {
			private static final String RAW_VALUES = "rawValues";
			
			@Override
			public void apply(HttpExchange httpExchange, RequestParser requestParser) throws SQLException, IOException, JSONException {
				List<Object> returnValueList = companiesMap.get(requestParser.getCompanyName()).readRow(requestParser.getTableName(),
																									requestParser.getQueryStringMap().get(PRIMARY_KEY_NAME),
																									requestParser.getQueryStringMap().get(PRIMARY_KEY_VALUE));
				
				String responseBody = responseBodyBuilder(RAW_VALUES, returnValueList.toString());
				sendMessage(httpExchange, HttpStatusCode.OK, responseBody); 
			}
		}

	}
	
	private class FieldNameHandler implements HttpHandler {
		private Map<HttpMethod, HttpMethodFunction<HttpExchange, RequestParser>> dbMethodMap = new HashMap<>();
		private static final String FIELD_VALUES = "fieldValues";

		private FieldNameHandler() {
			dbMethodMap.put(HttpMethod.GET, new ReadFieldHandler());
			dbMethodMap.put(HttpMethod.PUT, new UpdateFieldHandler());
		}

		@Override
		public void handle(HttpExchange httpexchange) throws IOException {
			httpExchangeHandle(httpexchange, dbMethodMap);
		}
		
		private class ReadFieldHandler implements HttpMethodFunction<HttpExchange, RequestParser> {
			@Override
			public void apply(HttpExchange httpExchange, RequestParser requestParser) throws SQLException, IOException, JSONException {
				Object returnValue = companiesMap.get(requestParser.getCompanyName()).readField(requestParser.getTableName(),
																							requestParser.getQueryStringMap().get(PRIMARY_KEY_NAME),
																							requestParser.getQueryStringMap().get(PRIMARY_KEY_VALUE),
																							requestParser.getQueryStringMap().get(COLUMN_NAME));

				
				String responseBody = responseBodyBuilder(FIELD_VALUES, returnValue.toString());
				sendMessage(httpExchange, HttpStatusCode.OK, responseBody); 
			}
		}
		
		private class UpdateFieldHandler implements HttpMethodFunction<HttpExchange, RequestParser> {
			private static final String ACK = "Field updated";
			
			@Override
			public void apply(HttpExchange httpExchange, RequestParser requestParser) throws SQLException, IOException, JSONException {
				JSONObject json = getBodyAsJSONObject(httpExchange);

				companiesMap.get(requestParser.getCompanyName()).updateField(json.getString(TABLE_NAME),
																			json.getString(PRIMARY_KEY_NAME),
																			json.get(PRIMARY_KEY_VALUE),
																			json.getString(COLUMN_NAME),
																			json.get(NEW_VALUE));

				sendMessage(httpExchange, HttpStatusCode.OK, ACK); 
			}
		}
	}
	
	private class FieldIndexHandler implements HttpHandler {
		private Map<HttpMethod, HttpMethodFunction<HttpExchange, RequestParser>> dbMethodMap = new HashMap<>();
		private static final String FIELD_VALUES = "fieldValues";

		private FieldIndexHandler() {
			dbMethodMap.put(HttpMethod.GET, new ReadFieldHandler());
			dbMethodMap.put(HttpMethod.PUT, new UpdateFieldHandler());
		}

		@Override
		public void handle(HttpExchange httpexchange) throws IOException {
			httpExchangeHandle(httpexchange, dbMethodMap);
		}
		
		private class ReadFieldHandler implements HttpMethodFunction<HttpExchange, RequestParser> {
			@Override
			public void apply(HttpExchange httpExchange, RequestParser requestParser) throws SQLException, IOException, JSONException {
				Object returnValue = companiesMap.get(requestParser.getCompanyName()).readField(requestParser.getTableName(),
																						requestParser.getQueryStringMap().get(PRIMARY_KEY_NAME),
																						requestParser.getQueryStringMap().get(PRIMARY_KEY_VALUE),
																						requestParser.getQueryStringMap().get(COLUMN_INDEX));
				String responseBody = responseBodyBuilder(FIELD_VALUES, returnValue.toString());
				sendMessage(httpExchange, HttpStatusCode.OK, responseBody); 
			}
		}
		
		private class UpdateFieldHandler implements HttpMethodFunction<HttpExchange, RequestParser> {
			private static final String ACK = "Field updated";
			
			@Override
			public void apply(HttpExchange httpExchange, RequestParser requestParser) throws SQLException, IOException, JSONException {
				JSONObject json = getBodyAsJSONObject(httpExchange);

				companiesMap.get(requestParser.getCompanyName()).updateField(json.getString(TABLE_NAME),
																			json.getString(PRIMARY_KEY_NAME),
																			json.get(PRIMARY_KEY_VALUE),
																			json.getInt(COLUMN_INDEX),
																			json.get(NEW_VALUE));

				sendMessage(httpExchange, HttpStatusCode.OK, ACK); 
			}
		}
	}
	
	private class IOTEventHandler implements HttpHandler {
		private Map<HttpMethod, HttpMethodFunction<HttpExchange, RequestParser>> dbMethodMap = new HashMap<>();
		
		private IOTEventHandler() {
			dbMethodMap.put(HttpMethod.POST, new CreateIOTEventHandler());
		}

		@Override
		public void handle(HttpExchange httpexchange) throws IOException {
			httpExchangeHandle(httpexchange, dbMethodMap);
		}
		
		private class CreateIOTEventHandler implements HttpMethodFunction<HttpExchange, RequestParser> {
			private static final String ACK = "IOT event created";
			private final static String RAW_DATA = "rawData";

			@Override
			public void apply(HttpExchange httpExchange, RequestParser requestParser) throws SQLException, IOException, JSONException {
				JSONObject json = getBodyAsJSONObject(httpExchange);
				companiesMap.get(requestParser.getCompanyName()).createIOTEvent(json.getString(RAW_DATA));

				sendMessage(httpExchange, HttpStatusCode.CREATED, ACK); 
			}
		}
	}
	
	private class RootHandler implements HttpHandler {
		private static final String ACK = "Options";

		private Map<HttpMethod, HttpMethodFunction<HttpExchange, RequestParser>> dbMethodMap = new HashMap<>();

		private RootHandler() {
			dbMethodMap.put(HttpMethod.OPTIONS, sendOptions);		
		}

		private HttpMethodFunction<HttpExchange, RequestParser> sendOptions = new HttpMethodFunction<>() {	
			@Override
			public void apply(HttpExchange httpExchange, RequestParser requestParser) throws IOException, SQLException {				
				httpExchange.getResponseHeaders().add("Allow", "GET, POST, PUT, DELETE, OPTIONS");
				sendMessage(httpExchange, HttpStatusCode.CREATED, ACK);
			}
		};
		
		@Override
		public void handle(HttpExchange httpExchange) throws IOException {
			
			try {
				HttpMethodFunction<HttpExchange, RequestParser> handler = dbMethodMap.get(HttpMethod.valueOf(httpExchange.getRequestMethod()));
				if(null != handler) {
					handler.apply(httpExchange, null);					
				} else {
					sendMessage(httpExchange, HttpStatusCode.NOT_IMPLEMENTED, ERROR);
				}
			} catch (SQLException | JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**********************************************
	 * HttpMethodFunction interface 
	 **********************************************/
	public interface HttpMethodFunction<T, U> {
		public void apply(HttpExchange httpExchange, RequestParser requestParser) throws SQLException, IOException, JSONException;
	}
	
	public class RequestParser {
		private Map<String, String> queryStringMap = new HashMap<>();
		private static final String FORWARD_SLASH = "[/]";
		private static final String AMPERSAND = "[&]";
		private static final String EQUAL = "[=]";
		private static final int KEY_ARG = 0;
		private static final int VALUE_ARG = 1;
		private static final int COMPANY_NAME_ARG = 2;
		private static final int TABLE_NAME_ARG = 3;
		private static final int LIMIT = 2;
		private String companyName;
		private String tableName;
		
		public RequestParser(URI uri, InputStream inputStream) throws IOException, JSONException {
			parsePath(uri.getPath());
			parseQueryString(uri.getQuery());
		}				
		
		private void parsePath(String path) {
			String[] requestTokens = path.split(FORWARD_SLASH);			
			companyName = requestTokens[COMPANY_NAME_ARG];
			tableName = requestTokens[TABLE_NAME_ARG];
		}
		
		private void parseQueryString(String query) {
			if(null != query) {
				String[] parameters = query.split(AMPERSAND);	
				String[] row;
				for(String parameter : parameters) {
					row = parameter.split(EQUAL, LIMIT);
					queryStringMap.put(row[KEY_ARG], row[VALUE_ARG]);
				}					
			}
		}
		
		private Map<String, String> getQueryStringMap() {
			return queryStringMap;
		}
		
		private String getCompanyName() {
			return companyName;
		}
		
		private String getTableName() {
			return tableName;
		}
		
	}
}
