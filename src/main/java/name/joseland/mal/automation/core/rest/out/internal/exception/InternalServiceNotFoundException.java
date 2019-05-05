package name.joseland.mal.automation.core.rest.out.internal.exception;

/**
 * A service that's part of the automation application couldn't be found.
 */
public class InternalServiceNotFoundException extends Exception {

    private final String resourceId;

    /**
     * @param resourceId the ID of the resource that couldn't be located.
     */
    public InternalServiceNotFoundException(String resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public String getMessage() {
        return "Failed to locate service with ID: " + resourceId;
    }

}
