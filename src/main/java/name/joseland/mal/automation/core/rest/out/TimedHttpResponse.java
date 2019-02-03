package name.joseland.mal.automation.core.rest.out;

import org.springframework.lang.NonNull;

import javax.net.ssl.SSLSession;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link HttpResponse} that stores the time requested and the time received.
 *
 * @param <T> the response body type
 */
public class TimedHttpResponse<T> implements HttpResponse<T>, Comparable<TimedHttpResponse<T>> {

    private final HttpResponse<T> httpResponse;
    private final LocalDateTime timeRequested;
    private final LocalDateTime timeReceived;


    private TimedHttpResponse(HttpResponse<T> httpResponse, LocalDateTime timeRequested, LocalDateTime timeReceived) {
        this.httpResponse = httpResponse;
        this.timeRequested = timeRequested;
        this.timeReceived = timeReceived;
    }

    /**
     * Static builder.
     *
     * @param httpResponse  the {@link HttpResponse} to build this object for (required)
     * @param timeRequested the time the request for the response was sent
     * @param timeReceived  the time the response was received
     * @param <U>           the response body type
     * @return              a valid TimedHttpResponse
     */
    public static <U> TimedHttpResponse<U> build(@NonNull HttpResponse<U> httpResponse,
                                                 @NonNull LocalDateTime timeRequested,
                                                 @NonNull LocalDateTime timeReceived) {
        if (timeRequested != null && timeReceived != null && timeRequested.isAfter(timeReceived))
            throw new IllegalArgumentException("Param timeRequested is before timeReceived");

        return new TimedHttpResponse<>(Objects.requireNonNull(httpResponse),
                Objects.requireNonNull(timeRequested), Objects.requireNonNull(timeReceived));
    }


    /* ********************************************************************************************************** */
    /* ********************************************* PUBLIC METHODS ********************************************* */
    /* ********************************************************************************************************** */


    @Override
    public String toString() {
        return "httpResponse=" + httpResponse.toString() +
                ", timeRequested=" + timeRequested.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) +
                ", timeReceived=" + timeReceived.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof TimedHttpResponse))
            return false;

        TimedHttpResponse otherTimedHttpResponse = (TimedHttpResponse) o;

        return System.identityHashCode(httpResponse) == System.identityHashCode(otherTimedHttpResponse.httpResponse) &&
                timeRequested.equals(otherTimedHttpResponse.timeRequested) &&
                timeReceived.equals(otherTimedHttpResponse.timeReceived);
    }

    @Override
    public int hashCode() {
        int hashCode = httpResponse.hashCode();
        hashCode = hashCode * 31 + timeRequested.hashCode();
        hashCode = hashCode * 31 + timeReceived.hashCode();

        return hashCode;
    }

    @Override
    public int compareTo(TimedHttpResponse<T> o) {
        int result = getTimeReceived().compareTo(o.getTimeReceived());
        if (result != 0)
            return result;

        result = getTimeRequested().compareTo(o.getTimeRequested());
        if (result != 0)
            return result;

        result = Integer.compare(System.identityHashCode(httpResponse),
                System.identityHashCode(o.httpResponse));
        if (result != 0)
            return result;

        return 0;
    }


    /* ********************************************************************************************************** */
    /* ********************************************* GETTERS/SETTERS ******************************************** */
    /* ********************************************************************************************************** */


    /**
     * @return the time the HTTP request was sent.
     */
    public LocalDateTime getTimeRequested() {
        return timeRequested;
    }

    /**
     * @return the time the HTTP response was received.
     */
    LocalDateTime getTimeReceived() {
        return timeReceived;
    }

    @Override
    public int statusCode() {
        return httpResponse.statusCode();
    }

    @Override
    public HttpRequest request() {
        return httpResponse.request();
    }

    @Override
    public Optional<HttpResponse<T>> previousResponse() {
        return httpResponse.previousResponse();
    }

    @Override
    public HttpHeaders headers() {
        return httpResponse.headers();
    }

    @Override
    public T body() {
        return httpResponse.body();
    }

    @Override
    public Optional<SSLSession> sslSession() {
        return httpResponse.sslSession();
    }

    @Override
    public URI uri() {
        return httpResponse.uri();
    }

    @Override
    public HttpClient.Version version() {
        return httpResponse.version();
    }

}
