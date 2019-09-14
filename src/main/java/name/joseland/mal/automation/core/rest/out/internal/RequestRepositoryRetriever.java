package name.joseland.mal.automation.core.rest.out.internal;

import com.fasterxml.jackson.databind.JsonNode;
import name.joseland.mal.automation.core.rest.out.BasicRequestSender;
import name.joseland.mal.automation.core.rest.out.JsonBodyHandler;
import name.joseland.mal.automation.core.rest.out.internal.exception.InternalServiceNotFoundException;
import name.joseland.mal.automation.core.rest.out.internal.exception.InvalidInternalServiceRequestParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Retrieves stored HTTP request from the request repository service.
 */
// TODO unit tests
@Component
public class RequestRepositoryRetriever {

    private final String requestRepositoryId;
    private final String requestRepositoryContextPath;

    private final InternalRequestUriAssembler internalRequestUriAssembler;


    RequestRepositoryRetriever(@Value("${automation.request-repository.id}") String requestRepositoryId,
                               @Value("${automation.request-repository.context-path}")
                                       String requestRepositoryContextPath,
                               @Autowired InternalRequestUriAssembler internalRequestUriAssembler) {
        this.requestRepositoryId = requestRepositoryId;
        this.requestRepositoryContextPath = requestRepositoryContextPath;
        this.internalRequestUriAssembler = internalRequestUriAssembler;
    }


    /**
     * Queries and returns a request stored in the repository.
     *
	 * @param type          tye type of the stored request
     * @param resourceId    the ID of the stored request eg. 52
     * @return              the retrieved request
     */
    public HttpRequest retrieveStoredRequest(StoredRequestType type, int resourceId) throws IOException,
            InterruptedException, InternalServiceNotFoundException, URISyntaxException,
            InvalidInternalServiceRequestParameter {
        // get the request repository URI
        URI requestRepositoryUri = internalRequestUriAssembler.fromResourcePath(requestRepositoryId,
                requestRepositoryContextPath + "/" + type.resourcePathSegment + "/" + resourceId);

        // get the response from the request repository
        HttpRequest request = HttpRequest.newBuilder()
                .uri(requestRepositoryUri)
                .header("Accept", "application/json")
                .GET()
                .build();
        BasicRequestSender<JsonNode> requestSender = BasicRequestSender.build(request, new JsonBodyHandler());
        HttpResponse<JsonNode> response = requestSender.call();

        // return a HttpRequest built from the resource retrieved from the request repository
        JsonNode body = response.body();
        return internalRequestFromJsonNode(body);
    }


    /* ********************************************************************************************************** */
    /* ********************************************* PRIVATE METHODS ******************************************** */
    /* ********************************************************************************************************** */


    /**
     * Assembles an {@link HttpRequest} from a {@link JsonNode} stored request that has been retrieved from the
     * repository.
     *
     * Example JSON object (required fields only):
     *  {
     *     "id": 2,
     *     "httpMethod": "GET",
     *     "serviceId": "data-source-monitor",
     *     "resource": "/http-monitors/2/perform-check",
     *     "body": {
     *         [body_fields]
     *     }
     * }
     *
     * @param requestAsJson the request in JSON format
     * @return              the assembled HttpRequest
     */
    private HttpRequest internalRequestFromJsonNode(JsonNode requestAsJson) throws URISyntaxException,
            InvalidInternalServiceRequestParameter, InternalServiceNotFoundException {
        String httpMethodStr = requestAsJson.get("httpMethod").asText();

        String serviceId = requestAsJson.get("serviceId").asText();
        if ("".equals(serviceId))
            throw new InvalidInternalServiceRequestParameter(requestRepositoryId,
                    "serviceId field not specified");

        String resourcePath = requestAsJson.get("resource").asText();
        if ("".equals(resourcePath))
            throw new InvalidInternalServiceRequestParameter(requestRepositoryId,
                    "resource field not specified");

        String bodyStr = requestAsJson.get("body").asText();

        URI internalRequestUri = internalRequestUriAssembler.fromResourcePath(serviceId, resourcePath);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(internalRequestUri)
                .header("Accept", "application/json");

        return addHttpMethod(requestBuilder, httpMethodStr, HttpRequest.BodyPublishers.ofString(bodyStr))
                .build();
    }

    /**
     * Adds a HTTP method to a {@link HttpRequest.Builder}.
     *
     * @param requestBuilder    the builder to add the method to
     * @param httpMethodStr     a string representation of the HTTP method
     * @param bodyPublisher     the body publisher for the HTTP method
     * @return                  the builder with the HTTP method added
     * @throws InvalidInternalServiceRequestParameter    if the builder throws an IllegalArgumentException when the
     *                                                    params are added
     */
    private HttpRequest.Builder addHttpMethod(HttpRequest.Builder requestBuilder,
                                              String httpMethodStr,
                                              HttpRequest.BodyPublisher bodyPublisher)
                throws InvalidInternalServiceRequestParameter {
        try {
            return requestBuilder.method(httpMethodStr, bodyPublisher);
        } catch (IllegalArgumentException e) {
            throw new InvalidInternalServiceRequestParameter(requestRepositoryId,
                    "failed to identify RequestMethod from httpMethod value: " + httpMethodStr);
        }
    }

}
