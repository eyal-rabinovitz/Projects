package gatewayserver;

public enum HttpVersion {
	HTTP_1_0("HTTP/1.0"),
    HTTP_1_1("HTTP/1.1");
	
	private String version;

	HttpVersion(String version) {
	    this.version = version;
	}

	public String getVersionAsString() {
	    return version;
	}
	
	public static HttpVersion getCurrHttpVersion(String httpVersion) {
		if (httpVersion.equals(HTTP_1_1.getVersionAsString())) {
			return HttpVersion.HTTP_1_1;
		}
		return HttpVersion.HTTP_1_0;
	}
}