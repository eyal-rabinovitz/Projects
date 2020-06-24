package il.co.ilrd.sunhttpserver;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class RequestParser {
	private Map<String, Object> jsonMap = new HashMap<>();
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
		parseBody(inputStream);						
	}				
	
	private void parseBody(InputStream inputStream) throws IOException, JSONException {
		if(inputStream.available() > 0) {
			byte[] bodyBytes = new byte[2048];
			while(-1 != inputStream.read(bodyBytes));
			JSONObject jsonBody = new JSONObject(new String(bodyBytes));			
			//jsonMap = jsonBody.toMap();	//TODO				
		}
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
	
	private Map<String, Object> getJsonMap() {
		return jsonMap;
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