package name.joseland.mal.automation.core.rest.out.internal;

import com.fasterxml.jackson.databind.JsonNode;
import name.joseland.mal.automation.core.rest.out.BasicRequestSender;
import name.joseland.mal.automation.core.rest.out.JsonBodyHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Retrieves stored HTTP request from the request repository service.
 */
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
     * @param resourcePath  the path of the stored request eg. "/internal-requests/52/assembled"
     *                          should exclude the repository's context path
     * @return              the retrieved request
     */
    public HttpRequest fromResourcePath(String resourcePath) throws IOException, InterruptedException {
        // get the request repository URI
        URI requestRepositoryUri = internalRequestUriAssembler.fromResourcePath(requestRepositoryId,
                requestRepositoryContextPath + resourcePath);

        // get the response from the request repository
        HttpRequest request = HttpRequest.newBuilder()
                .uri(requestRepositoryUri)
                .header("Accept", "application/json")
                .GET()
                .build();
        BasicRequestSender<JsonNode> requestSender = BasicRequestSender.build(request, new JsonBodyHandler());
        HttpResponse<JsonNode> response = requestSender.call();

        JsonNode body = response.body();

        return fromJsonNode(body);
    }


    /* ********************************************************************************************************** */
    /* ********************************************* PRIVATE METHODS ******************************************** */
    /* ********************************************************************************************************** */


    /**
     * Assembles an {@link HttpRequest} from a {@link JsonNode} stored request that has been retrieved from the
     * repository.
     *
     * Required JSON fields:
     *  . TODO
     *
     * @param requestAsJson the request in JSON format
     * @return              the assembled HttpRequest
     */
    private HttpRequest fromJsonNode(JsonNode requestAsJson) {
        // TODO
        return null;
    }

}
