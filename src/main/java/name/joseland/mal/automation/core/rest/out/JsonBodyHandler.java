package name.joseland.mal.automation.core.rest.out;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import name.joseland.mal.automation.core.rest.HttpRequestException;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpResponse;

/**
 * {@link HttpResponse.BodyHandler} implementation.
 */
public class JsonBodyHandler implements HttpResponse.BodyHandler<JsonNode> {

    @Override
    public HttpResponse.BodySubscriber<JsonNode> apply(HttpResponse.ResponseInfo responseInfo) {
        HttpResponse.BodySubscriber<InputStream> stringSubscriber = HttpResponse.BodyHandlers.ofInputStream()
                .apply(responseInfo);

        return HttpResponse.BodySubscribers.mapping(stringSubscriber,
                JsonBodyHandler::jsonFromInputStream);
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
