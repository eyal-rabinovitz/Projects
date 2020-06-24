package il.co.ilrd.http_message;

import java.util.Map;

public class HttpBuilder {
	private final static String NEW_LINE = "\r\n";
	private final static String HEADER_SEPERATOR = ": ";
	private final static String EMPTY_STRING = "";
	private final static String SPACE = " ";

	public static String createHttpRequestMessage(HttpMethod method,
													HttpVersion version, 
													String url, 
													Map<String, String> header, 
													String body) {
		return
			StartLineBuilder.createStartLineRequest(method, version, url) +
			HeaderBuilder.createHeader(header) + 
			NEW_LINE +
			BodyBuilder.createBody(body);
	}
	
	public static String createHttpResponseMessage(HttpVersion version,
													HttpStatusCode code,
													Map<String, String> header,
													String body) {
		return
			StartLineBuilder.createStartLineResponse(version, code) +
			HeaderBuilder.createHeader(header) +
			NEW_LINE +
			BodyBuilder.createBody(body);
	}
	
	public static class StartLineBuilder {
		public static String createStartLineRequest(HttpMethod method, HttpVersion version, String url) {
			return method.getMethodAsString() + SPACE + url + SPACE + version.getVersionAsString() + NEW_LINE;
		}
		
		public static String createStartLineResponse(HttpVersion version, HttpStatusCode code) {
			return version.getVersionAsString() + SPACE + code.asText() + SPACE + code.getDescription() + NEW_LINE;
		}
	}
	
	public static class HeaderBuilder {
		public static String createHeader(Map<String, String> headerMap) {
			String headers = EMPTY_STRING;
			if(null != headerMap && !headerMap.isEmpty()) {
				for (String key: headerMap.keySet()) {
					headers += key + HEADER_SEPERATOR + headerMap.get(key) + NEW_LINE;
		        }
			}
			return headers;
		}
	}
	
	public static class BodyBuilder {
		public static String createBody(String body) {
			if(null == body) {
				return EMPTY_STRING;
			}
			return body;
		}
	}
}