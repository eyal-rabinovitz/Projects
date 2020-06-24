package il.co.ilrd.sunhttpserver;

import java.util.HashMap;
import java.util.Map;

public class HTTPMessageParser {
	private HttpParser httpParser;
	private URLParser urlParse;
	
	public HTTPMessageParser(String message){
		httpParser = new HttpParser(message);
		urlParse = new URLParser(httpParser.getStartLine().getURL());
	}
	
	public HttpParser getHttpParser() {
		return httpParser;
	}

	public void setHttpParser(HttpParser httpParser) {
		this.httpParser = httpParser;
	}

	public URLParser getUrlParse() {
		return urlParse;
	}

	public void setUrlParse(URLParser urlParse) {
		this.urlParse = urlParse;
	}

	public class HttpParser {
		private static final String EMPTY = "";
		private static final String CRLF = "\r\n";
		private static final String EMPTY_LINE = CRLF + CRLF;
		private static final int LIMIT = 2;
		private StartLineParser startLine;
		private HeaderParser header = null;
		private BodyParser body = null;
		
		public HttpParser(String message) {
			String[] messageParts = message.split(CRLF, LIMIT);
			startLine = new StartLineParser(messageParts[0]);	
			header = new HeaderParser(messageParts[1]);
			body = new BodyParser(messageParts[1]);
		}

		public StartLineParser getStartLine() {
			return startLine;
		}

		public HeaderParser getHeader() {
			return header;
		}

		public BodyParser getBody() {
			return body;
		}

		public boolean isRequest() {
			return startLine.isRequest();
		}
		
		public boolean isReply() {
			return !isRequest();
		}
		
		public class StartLineParser {
			private static final String SPACE = " ";
			private static final String HTTP_RESPONSE_PREFIX = "HTTP";
			private HttpMethod httpMethod = null;
			private HttpVersion httpVersion = null;
			private HttpStatusCode httpStatusCode = null;
			private String url = EMPTY;
			private boolean isResponse;

			public StartLineParser(String startLine) {
				String[] startLineParts = startLine.split(SPACE, 3);
				if(isResponse(startLine)) {
					initResponseStartLine(startLineParts);				
				} else {
					initRequestStartLine(startLineParts);
				}
			}
			
			public HttpMethod getHttpMethod() {
				return httpMethod;
			}
			
			public String getURL() {
				return url;
			}
			
			public HttpStatusCode getStatus() {
				return httpStatusCode;
			}
			
			public boolean isRequest() {
				return !isResponse;
			}
			
			public boolean isReply() {			
				return isResponse;
			}
			
			public HttpVersion getHttpVersion() {
				return httpVersion;
			}
			
			private boolean isResponse(String startLine) {
				if(startLine.startsWith(HTTP_RESPONSE_PREFIX)) {
					isResponse = true;
					return true;
				}
				
				isResponse = false;
				return false;
			}
			
			private void initResponseStartLine(String[] startLineParts) {
				httpVersion = getCurrHttpVersion(startLineParts[0]);
				httpStatusCode = getStatusCodeEnum(Integer.parseInt(startLineParts[1]));
				url = null;
			}

			private void initRequestStartLine(String[] startLineParts) {
				httpMethod = HttpMethod.valueOf(startLineParts[0].trim());			
				httpVersion = getCurrHttpVersion(startLineParts[2]);
				url = startLineParts[1];				
			}

			private HttpStatusCode getStatusCodeEnum(int code) {
		    	for(HttpStatusCode statusIterCode : HttpStatusCode.values()) {
		    		if (code == statusIterCode.getCode()) {
		    			return statusIterCode;
		    		}
		    	}
		    	return null;
		    }
			
			private HttpVersion getCurrHttpVersion(String httpVersion) {
				for (HttpVersion versionIter : HttpVersion.values()) {
					if (versionIter.getVersionAsString().equals(httpVersion)) {
						return versionIter;
					}					
				}
				return null;
			}	
		}
		
		public class HeaderParser {
			private static final String COLON = ": ";
			private Map<String, String> headersMap = new HashMap<>();
			
			public HeaderParser(String headerPart) {
				if (isHeaderExist(headerPart)) { 
					String[] messageParts = headerPart.split(EMPTY_LINE, LIMIT);
					String[] headers = messageParts[0].split(CRLF);
					for (int i = 0; i < headers.length; ++i) {
						addHeaderToMap(headers[i]);
					}
				}
			}
		
			public String getHeader(String header) {
				return headersMap.get(header);
			}
		
			public Map<String, String> getAllHeaders() {
				return headersMap;
			}
			
			private boolean isHeaderExist(String headerPart) {
				return (!headerPart.equals(CRLF));
			}
			
			private void addHeaderToMap(String header) {
				String[] headerParts = header.split(COLON, LIMIT);
				headersMap.put(headerParts[0], headerParts[1]);
			}
		}
		
		public class BodyParser {
			private String bodyString = EMPTY;
			
			public BodyParser(String message) {
				String[] messageParts = message.split(EMPTY_LINE, LIMIT);
				if(isBodyExist(messageParts)) {
					this.bodyString = messageParts[1];
				}
			}
		
			public String getBodyString() {
				return bodyString;
			}
			
			private boolean isBodyExist(String[] messageParts) {
				return (messageParts.length == 2 && !messageParts[1].trim().isEmpty());
			}
		}	
	}
	
	public class URLParser {
		
		private String databaseName = null;
		private String tableName = null;
		private DatabaseKeys databaseKeys = null;
		private Map<String, String> paramsMap = new HashMap<>();
		
		public URLParser(String url) {
			url = url.substring(1);
			if(!url.isEmpty()) {	
				String[] urlArr = url.split("/", 3);
				databaseName = urlArr[0];
				tableName = urlArr[1];
				
				String[] keyAndParams = urlArr[2].split("\\?", 2);
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

}
