package name.joseland.mal.automation.core.rest.out;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import name.joseland.mal.automation.core.rest.HttpRequestException;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpResponse;

/**
 * {@link HttpResponse.BodyHandler} implementation for a response with a JSON-formatted body.
 */
// TODO unit tests
public class JsonBodyHandler implements HttpResponse.BodyHandler<JsonNode> {

    /**
     * Returns a {@link HttpResponse.BodySubscriber<JsonNode>} that can convert response body bytes into a
     * {@link JsonNode}.
	 *
     * @throws JsonParseException  if the response format can't be parsed as JSON
	 */
    @Override
    public HttpResponse.BodySubscriber<JsonNode> apply(HttpResponse.ResponseInfo responseInfo) {
        HttpResponse.BodySubscriber<InputStream> stringSubscriber = HttpResponse.BodyHandlers.ofInputStream()
                .apply(responseInfo);

        return HttpResponse.BodySubscribers.mapping(stringSubscriber, JsonBodyHandler::jsonFromInputStream);
    }


    /* ********************************************************************************************************** */
    /* ********************************************* PRIVATE METHODS ******************************************** */
    /* ********************************************************************************************************** */


    private static JsonNode jsonFromInputStream(InputStream is) {
        try {
            return new ObjectMapper().readTree(is);
        } catch (IOException e) {
            throw new HttpRequestException(e.getMessage());
        }
    }

}
