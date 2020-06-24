package il.co.ilrd.gatewayserver;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HTTPMessageParser {
	private static final String CRLF = "\\r\\n";
	private StartLine startLine;
	private Header header = null;
	private Body body = null;
	private List<String> allLinesInMessage = new ArrayList<>(); 
	private static final int START_LINE_INDEX = 0;
	
	public HTTPMessageParser(String message) {
		parseMessage(message);
	}
 
	public HTTPMessageParser() {
		
	}
	
	public void parseMessage(String message)  {
		allLinesInMessage = Arrays.asList(message.split(CRLF));		
		parseStartLine();
		int emptyLineIndex = findEmptyLineIndex();
		header = null;
		body = null;
		if (emptyLineIndex != 0) {
			parseHeader(emptyLineIndex);
			parseBody(emptyLineIndex);
		}		
	}

	private void parseStartLine() {
		startLine = new StartLine(allLinesInMessage.get(START_LINE_INDEX));		
	}

	private void parseHeader(int emptyLineIndex)  {
		List<String> headerPart = getHeaderPart(emptyLineIndex);
		if (headerPart.isEmpty()){
			header = null;
		}
		else {
			header = new Header(headerPart);
		}		
	}
	
	private List<String> getHeaderPart(int emptyLineIndex)  {
		return allLinesInMessage.subList(1, emptyLineIndex);
	}
	
	private void parseBody(int emptyLineIndex) {
		String bodyPart = getBodyPart(emptyLineIndex);
		if (!hasBody(bodyPart)){
			body = null;
		}
		else {
			body = new Body(bodyPart);
		}
	}

	private boolean hasBody(String bodyPart) {		
		return !bodyPart.trim().isEmpty();
	}
	
	private String getBodyPart(int emptyLineIndex) {
		List<String> bodyPartLines = allLinesInMessage.subList(emptyLineIndex + 1, allLinesInMessage.size()); 
		return String.join(CRLF, bodyPartLines);
	}
	
	private int findEmptyLineIndex() {
		for (int emptyLineIndex = 1; emptyLineIndex < allLinesInMessage.size(); emptyLineIndex++) {
			if (allLinesInMessage.get(emptyLineIndex).length() == 0) {
				return emptyLineIndex;
			}
		}
		return 0;
	}

	public StartLine getStartLine() {
		return startLine;
	}

	public Header getHeader() {
		return header;
	}

	public Body getBody() {
		return body;
	}

	boolean isRequest() {
		return startLine.isRequest();
	}
	
	boolean isReply() {
		return startLine.isReply();
	}

	public class StartLine {
		private final List<String> httpVersions =  getAllHttpVersions();
		private HttpVersion httpVersion;
		private HttpMethod httpMethod;
		private HttpStatusCode statusCode;
		private String url;
		private String [] startLineParts;
		private static final int REQUEST_MESSAGE_VERSION_POSITION = 2;
		private static final int RESPONSE_MESSAGE_VERSION_POSITION = 0;
		private static final int RESPONSE_MESSAGE_STATUS_CODE_POSITION = 1;
		private static final int REQUEST_MESSAGE_URL_POSITION = 1;
		private static final int REQUEST_MESSAGE_METHOD_POSITION = 0;
		private static final String SPACE = "\\s";

		public StartLine (String startLineString) {
			try {
				parseStartLine(startLineString);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		
		private void parseStartLine(String startLineString) throws MalformedURLException {
			startLineParts = startLineString.split(SPACE, 3);
			if (isRequest()) {
				handleRequestMessage(startLineParts);
			} else {
				handleResponseMessage(startLineParts);
			}			
		}	
		
		public HttpMethod getHttpMethod() {			
			return httpMethod;
		}
		
		public String getURL() {
			return url;
		}
		
		public HttpStatusCode getStatus() {
			return statusCode;
		}
		
		public boolean isRequest() {
			return !isReply();
		}
		
		public boolean isReply() {
			return httpVersions.contains(startLineParts[RESPONSE_MESSAGE_VERSION_POSITION]);
		}
		
		public HttpVersion getHttpVersion() {
			return httpVersion;
		}
		
		private List<String> getAllHttpVersions() {
			List<String> httpVersions = new LinkedList<>();
			for (HttpVersion version : HttpVersion.values()) {
				httpVersions.add(version.getVersionAsString());
			}
			return httpVersions;
		}
		
		private void handleRequestMessage(String[] startLineParts) throws MalformedURLException {
			updateHttpMethod(startLineParts[REQUEST_MESSAGE_METHOD_POSITION]);
			updateUrl(startLineParts[REQUEST_MESSAGE_URL_POSITION]);
			updateHttpVersion(startLineParts[REQUEST_MESSAGE_VERSION_POSITION]);			
		}
		
		private void updateHttpMethod(String methodString) {
			httpMethod = findHttpMethod(methodString);
		}

		private void updateUrl(String urlString) {
			url = urlString;
		}
		
		private void updateHttpVersion(String versionString) {
			httpVersion = findHttpVersion(versionString);
		}
		
		private void handleResponseMessage(String[] startLineParts) {
			updateHttpVersion(startLineParts[RESPONSE_MESSAGE_VERSION_POSITION]);
			updateStatusCode(startLineParts[RESPONSE_MESSAGE_STATUS_CODE_POSITION]);
		}

		private void updateStatusCode(String code) {
			statusCode = findStatusCode(code);
		}

		private HttpStatusCode findStatusCode(String code) {
			for (HttpStatusCode statusCode : HttpStatusCode.values()) {
				if (statusCode.asText().equals(code)){
					return statusCode;
				}
			}
			return null;			
		}
		
		private HttpMethod findHttpMethod(String methodString) {
			for (HttpMethod method : HttpMethod.values()) {
				if (method.getMethodAsString().equals(methodString)){
					return method;
				}
			}
			return null;			
		}
		
		private HttpVersion findHttpVersion(String versionString) {
			for (HttpVersion version : HttpVersion.values()) {
				if (version.getVersionAsString().equals(versionString)){
					return version;
				}
			}
			return null;			
		}
	}
	
	public class Header {
		private Map<String, String> headers = new HashMap<>();		
		private static final String HEADER_DELIMITER = ":";
		
		public Header(List<String> headerPart) {
			createHeadersMap(headerPart);
		}
		
		public String getHeader(String header) {
			return headers.get(header);
		}
	
		public Map<String, String> getAllHeaders() {
			return headers;
		}
		
		private void createHeadersMap(List<String> headerPart) {
			for (String headerLine : headerPart) {
				String [] headerLineParts = headerLine.split(HEADER_DELIMITER, 2);
				headers.put(headerLineParts[0], headerLineParts[1]);
			}
		}
	}
	
	public class Body {
		String bodyText;
		
		public Body(String bodyText) {
			this.bodyText = bodyText;
		}
	
		public String getBodyText() {
			return bodyText;
		}
	}
}