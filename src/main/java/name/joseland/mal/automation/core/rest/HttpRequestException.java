package name.joseland.mal.automation.core.rest;

/**
 * A failure to retrieve a result from a HTTP request.
 */
public class HttpRequestException extends RuntimeException {

    private final String message;

    public HttpRequestException(String message) {
        this.message = message;
    }


    /* ********************************************************************************************************** */
    /* ********************************************* PUBLIC METHODS ********************************************* */
    /* ********************************************************************************************************** */


    @Override
    public String getMessage() {
        return message;
    }

}
