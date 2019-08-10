package name.joseland.mal.automation.requestrepo;

import name.joseland.mal.automation.core.rest.out.internal.HttpRequestDto;
import name.joseland.mal.automation.core.rest.out.internal.ServiceInstanceLocator;
import name.joseland.mal.automation.core.rest.out.internal.exception.InternalServiceNotFoundException;
import name.joseland.mal.automation.requestrepo.db.ExternalRequest;
import name.joseland.mal.automation.requestrepo.db.InternalRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Objects;

/**
 * Contains single-step builders for {@link HttpRequestDto} instances.
 */
@Component
public class HttpRequestDtoBuilder {

	private final ServiceInstanceLocator serviceInstanceLocator;

	HttpRequestDtoBuilder(@Autowired ServiceInstanceLocator serviceInstanceLocator) {
		this.serviceInstanceLocator = serviceInstanceLocator;
	}


	/**
	 * Static builder from {@link InternalRequest}.
	 *
	 * @param internalRequest	an internal request
	 * @return					the request DTO
	 */
	public HttpRequestDto fromInternalRequest(InternalRequest internalRequest)
			throws InternalServiceNotFoundException {
		Objects.requireNonNull(internalRequest);

		// find a ServiceInstance from the internalRequest's serviceId
		ServiceInstance serviceInstance = serviceInstanceLocator.findInstance(internalRequest.getServiceId());

		String url = serviceInstance.getUri() + internalRequest.getResource();

		HashMap<String, String> headerHeaders = new HashMap<>();
		headerHeaders.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

		return new HttpRequestDto(internalRequest.getHttpMethod(), url,
				headerHeaders, internalRequest.getBody().toString());
	}

	/**
	 * Static builder from {@link ExternalRequest}.
	 *
	 * @param externalRequest	an external request
	 * @return					the request DTO
	 */
	public HttpRequestDto fromExternalRequest(ExternalRequest externalRequest) {
		Objects.requireNonNull(externalRequest);

		return new HttpRequestDto(externalRequest.getHttpMethod(), externalRequest.getUrl(),
				externalRequest.getHeaderFields(), externalRequest.getBody());
	}
}
