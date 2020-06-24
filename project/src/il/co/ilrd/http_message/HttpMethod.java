package il.co.ilrd.http_message;

public enum HttpMethod {
	GET("GET"),
	POST("POST"),
	PUT("PUT"),
	DELETE("DELETE"),
	OPTIONS("OPTIONS"),
	TRACE("TRACE"),
	PATCH("PATCH"),
	HEAD("HEAD")
	;
	
	private String method;

	HttpMethod(String method) {
	    this.method = method;
	}

	public String getMethodAsString() {
	    return method;
	}
}