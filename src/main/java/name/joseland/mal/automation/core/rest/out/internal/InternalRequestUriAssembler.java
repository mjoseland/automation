package name.joseland.mal.automation.core.rest.out.internal;

import name.joseland.mal.automation.core.rest.out.internal.exception.InternalServiceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Finds a {@link URI} from an ID of an internal service and a resource path.
 */
@Component
public class InternalRequestUriAssembler {

    private final ServiceInstanceLocator serviceInstanceLocator;


    InternalRequestUriAssembler(@Autowired ServiceInstanceLocator serviceInstanceLocator) {
        this.serviceInstanceLocator = serviceInstanceLocator;
    }

    public URI fromResourcePath(String resourceId, String resourcePath)
            throws InternalServiceNotFoundException, URISyntaxException {
        ServiceInstance serviceInstance = serviceInstanceLocator.findInstance(resourceId);

        URI serviceUri = serviceInstance.getUri();
        return new URI(serviceUri.toString() + resourcePath);
    }


}
