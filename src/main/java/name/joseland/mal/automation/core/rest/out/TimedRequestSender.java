package name.joseland.mal.automation.core.rest.out;

import org.springframework.lang.NonNull;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Immutable callable that sends a HTTP request and eventually returns a {@link TimedHttpResponse}.
 *
 * @param <T> the response body type
 */
public class TimedRequestSender<T> implements RequestSender<TimedHttpResponse<T>, T> {

    private final HttpClient client;
    private final HttpRequest request;
    private final HttpResponse.BodyHandler<T> bodyHandler;


    private TimedRequestSender(@NonNull HttpClient client,
                               @NonNull HttpRequest request,
                               @NonNull HttpResponse.BodyHandler<T> bodyHandler) {
        this.client = client;
        this.request = request;
        this.bodyHandler = bodyHandler;
    }

    /**
     * Static builder.
     *
     * @param request       the HTTP request to send
     * @param bodyHandler   the response BodyHandler
     * @param <U>           the response body type
     * @return              the new TimedRequestSender
     */
    public static <U> TimedRequestSender<U> build(@NonNull HttpRequest request,
                                                  @NonNull HttpResponse.BodyHandler<U> bodyHandler) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(bodyHandler);

        return new TimedRequestSender<>(HttpClient.newHttpClient(), request, bodyHandler);
    }



    /* ********************************************************************************************************** */
    /* ********************************************* PUBLIC METHODS ********************************************* */
    /* ********************************************************************************************************** */


    /**
     * Performs the request and return the response.
     *
     * @return                          the response
     * @throws IOException              if an I/O error occurs when sending or receiving
     * @throws InterruptedException     if the request is interrupted
     */
    @Override
    public TimedHttpResponse<T> call() throws IOException, InterruptedException {
        var timeRequested = LocalDateTime.now();

        HttpResponse<T> response = client.send(request, bodyHandler);

        return TimedHttpResponse.build(response, timeRequested, LocalDateTime.now());
    }

}
