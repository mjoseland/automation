package name.joseland.mal.automation.core.rest.out;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import name.joseland.mal.automation.core.rest.HttpRequestException;

import java.io.IOException;
import java.net.http.HttpResponse;

/**
 * {@link HttpResponse.BodyHandler} implementation for a response with a JSON-formatted body.
 *
 * TODO implement mapping from BodyHandlers::ofInputStream instead of BodyHandlers::ofString
 */
public class JsonBodyHandler implements HttpResponse.BodyHandler<JsonNode> {

    /**
     * Returns a {@link HttpResponse.BodySubscriber<JsonNode>} that can convert response body bytes into a
     * {@link JsonNode}.
	 *
     * @param responseInfo the info of the response to apply the body handler to
     */
    @Override
    public HttpResponse.BodySubscriber<JsonNode> apply(HttpResponse.ResponseInfo responseInfo) {
        HttpResponse.BodySubscriber<String> stringSubscriber = HttpResponse.BodyHandlers.ofString()
                .apply(responseInfo);

        return HttpResponse.BodySubscribers.mapping(stringSubscriber,
                JsonBodyHandler::jsonFromString);
    }


    /* ********************************************************************************************************** */
    /* ********************************************* PRIVATE METHODS ******************************************** */
    /* ********************************************************************************************************** */


    private static JsonNode jsonFromString(String s) {
        try {
            return new ObjectMapper().readTree(s);
        } catch (IOException e) {
            throw new HttpRequestException(e.getMessage());
        }
    }

}
