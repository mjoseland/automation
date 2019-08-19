package name.joseland.mal.automation.core.rest.out.internal;

import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * Immutable DTO that can be used to assemble an {@link java.net.http.HttpRequest}.
 */
public class HttpRequestDto {

	private final HttpMethod httpMethod;
	private final String url;
	private final Map<String, String> headerFields;
	private final String body;

	public HttpRequestDto(HttpMethod httpMethod, String url, Map<String, String> headerFields, String body) {
		this.httpMethod = httpMethod;
		this.url = url;
		this.headerFields = headerFields;
		this.body = body;
	}


	/* ********************************************************************************************************** */
	/* ********************************************* GETTERS/SETTERS ******************************************** */
	/* ********************************************************************************************************** */


	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public String getUrl() {
		return url;
	}

	public Map<String, String> getHeaderFields() {
		return new HashMap<>(headerFields);
	}

	public String getBody() {
		return body;
	}

}
