package il.co.ilrd.sunhttpserver;

import java.util.HashMap;
import java.util.Map;

public class URLParser {
	
	private String databaseName = null;
	private String tableName = null;
	private String section = null; //TODO find a better name
	private DatabaseKeys databaseKeys = null;
	private Map<String, String> paramsMap = new HashMap<>();
	
	public URLParser(String url) {
		url = url.substring(1);
		if(!url.isEmpty()) {	
			String[] urlArr = url.split("/", 4);
			section = urlArr[0];
			databaseName = urlArr[1];
			tableName = urlArr[2];
			
			String[] keyAndParams = urlArr[3].split("\\?", 2);
			databaseKeys = getKeyByString(keyAndParams[0]);
			
			if(keyAndParams.length > 1) {
				String[] params = keyAndParams[1].split("\\&");
				String[] paramParts;
				for(int i = 0; i < params.length; ++i) {
					paramParts = params[i].split("=");
					paramsMap.put(paramParts[0], paramParts[1]);
				}
			}
		}
	}
	

	private DatabaseKeys getKeyByString(String keysAsString) {
		for(DatabaseKeys iter : DatabaseKeys.values()) {
			if(iter.toString().equals(keysAsString)) {
				return iter;
			}
		}
		
		return null;
	}

	public String getSection() {
		return section;
	}
	
	public void setSection(String section) {
		this.section = section;
	}
	
	public String getDatabaseName() {
		return databaseName;
	}
	
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public DatabaseKeys getDatabaseKeys() {
		return databaseKeys;
	}
	
	public void setDatabaseKeys(DatabaseKeys databaseKeys) {
		this.databaseKeys = databaseKeys;
	}
	
	public Map<String, String> getParamsMap() {
		return paramsMap;
	}
	
	public void setParamsMap(Map<String, String> paramsMap) {
		this.paramsMap = paramsMap;
	}
}
