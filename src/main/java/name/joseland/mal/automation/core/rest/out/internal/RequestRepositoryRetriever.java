package name.joseland.mal.automation.core.rest.out.internal;

import com.fasterxml.jackson.databind.JsonNode;
import name.joseland.mal.automation.core.rest.out.BasicRequestSender;
import name.joseland.mal.automation.core.rest.out.JsonBodyHandler;
import name.joseland.mal.automation.core.rest.out.internal.exception.InternalServiceNotFoundException;
import name.joseland.mal.automation.core.rest.out.internal.exception.InvalidInternalServiceRequestParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

/**
 * Retrieves stored HTTP request from the request repository service.
 */
@Component
public class RequestRepositoryRetriever {

    private final String requestRepositoryId;
    private final String requestRepositoryContextPath;
    private final String assembledRequestNoun;

    private final InternalRequestUriAssembler internalRequestUriAssembler;


    RequestRepositoryRetriever(@Value("${automation.request-repository.id}") String requestRepositoryId,
                               @Value("${automation.request-repository.context-path}")
                                       String requestRepositoryContextPath,
                               @Value("${automation.request-repository.assembled-request-noun}") String assembledRequestNoun,
                               @Autowired InternalRequestUriAssembler internalRequestUriAssembler) {
        this.requestRepositoryId = requestRepositoryId;
        this.requestRepositoryContextPath = requestRepositoryContextPath;
        this.assembledRequestNoun = assembledRequestNoun;
        this.internalRequestUriAssembler = internalRequestUriAssembler;
    }


    /**
     * Retrieves and a request stored in the request repository and returns it as a constructed
     * {@link HttpRequest}.
     *
     * @param resourceLink  eg. /internal-requests/52
     * @return              the retrieved and assembled request
     */
    public HttpRequest getConstructedRequest(String resourceLink) throws IOException, InterruptedException,
            InternalServiceNotFoundException, URISyntaxException, InvalidInternalServiceRequestParameter {
        // get a URI to retrieve the assembled request stored in the body as JSON
        String resourcePath = requestRepositoryContextPath + resourceLink + assembledRequestNoun;
        URI requestRepositoryUri = internalRequestUriAssembler.fromResourcePath(requestRepositoryId, resourcePath);

        // get the response from the request repository
        HttpRequest request = HttpRequest.newBuilder()
				.GET()
                .uri(requestRepositoryUri)
                .header("Accept", MediaType.APPLICATION_JSON)
                .build();
        BasicRequestSender<JsonNode> requestSender = BasicRequestSender.build(request, new JsonBodyHandler());
        HttpResponse<JsonNode> response = requestSender.call();

        // return a HttpRequest built from the resource retrieved from the request repository
        JsonNode body = response.body();
        return requestFromJsonNode(body);
    }


    /* ********************************************************************************************************** */
    /* ********************************************* PRIVATE METHODS ******************************************** */
    /* ********************************************************************************************************** */


    /**
     * Assembles an {@link HttpRequest} from a {@link JsonNode} stored request that has been retrieved from the
     * repository.
     *
     * Example JSON objects:
     *  {
     *     "httpMethod": "GET",
     *     "url": "http://www.test.com/"
     *  }
     *  {
     *     "httpMethod": "POST",
     *     "url": "http://10.6.2.55/http-monitors/2/monitor",
     *     "body": {
     *         [body_fields]
     *     }
     *  }
     *  {
     *     "httpMethod": "PUT",
     *     "url": "http://10.6.2.58/email-notification-config/2/notifier",
     *     "body": {
     *         [body_fields]
     *     }
     *  }
     *
     * @param requestAsJson the request in JSON format
     * @return              the assembled HttpRequest
     */
    private HttpRequest requestFromJsonNode(JsonNode requestAsJson) throws InvalidInternalServiceRequestParameter {
        String httpMethodStr = requestAsJson.get("httpMethod").asText();
        String uriStr = requestAsJson.get("url").textValue();

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(uriStr))
                .header("Accept", "application/json");

        Optional<JsonNode> bodyNodeOpt = Optional.ofNullable(requestAsJson.get("body"));

        HttpRequest.BodyPublisher bodyPublisher = bodyNodeOpt
                .map(jsonNode -> HttpRequest.BodyPublishers.ofString(jsonNode.toString()))
                .orElseGet(HttpRequest.BodyPublishers::noBody);

        return addHttpMethod(requestBuilder, httpMethodStr, bodyPublisher).build();
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
