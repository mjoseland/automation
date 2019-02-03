package name.joseland.mal.automation.core.rest.out;

import org.springframework.lang.NonNull;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

/**
 * Implementation that returns a {@link HttpResponse}
 *
 * @param <T> the response body type
 */
public class BasicRequestSender<T> implements RequestSender<HttpResponse<T>, T> {


    private final HttpClient client;
    private final HttpRequest request;
    private final HttpResponse.BodyHandler<T> bodyHandler;


    private BasicRequestSender(@NonNull HttpClient client,
                               @NonNull HttpRequest request,
                               @NonNull HttpResponse.BodyHandler<T> bodyHandler) {
        this.client = client;
        this.request = request;
        this.bodyHandler = bodyHandler;
    }

    public static <U> BasicRequestSender<U> build(@NonNull HttpRequest request,
                                                  @NonNull HttpResponse.BodyHandler<U> bodyHandler) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(bodyHandler);

        return new BasicRequestSender<>(HttpClient.newHttpClient(), request, bodyHandler);
    }


    /* ********************************************************************************************************** */
    /* ********************************************* PUBLIC METHODS ********************************************* */
    /* ********************************************************************************************************** */


    @Override
    public HttpResponse<T> call() throws IOException, InterruptedException {
        return client.send(request, bodyHandler);
    }

}
