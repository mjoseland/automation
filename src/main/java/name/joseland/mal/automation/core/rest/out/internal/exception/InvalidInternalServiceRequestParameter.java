package name.joseland.mal.automation.core.rest.out.internal.exception;

/**
 * Failed to generate an internal service request due to invalid parameter/s.
 */
public class InvalidInternalServiceRequestParameter extends Exception {

    private final String serviceId;
    private final String message;


    public InvalidInternalServiceRequestParameter(String serviceId, String message) {
        this.serviceId = serviceId;
        this.message = message;
    }


    @Override
    public String getMessage() {
        return message + "(serviceId=" + serviceId + ")";
    }

}
