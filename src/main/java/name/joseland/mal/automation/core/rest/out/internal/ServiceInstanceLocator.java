package name.joseland.mal.automation.core.rest.out.internal;

import name.joseland.mal.automation.core.rest.out.internal.exception.InternalServiceNotFoundException;
import org.springframework.cloud.client.ServiceInstance;

/**
 * Used to find services that are part of the automation application.
 */
public interface ServiceInstanceLocator {

    /**
     * Find a ServiceInstance for an internal service.
     *
     * @param resourceId    the ID of the service to find
     * @return              the located service's {@link ServiceInstance}
     * @throws InternalServiceNotFoundException if the service couldn't be found
     */
    ServiceInstance findInstance(String resourceId) throws InternalServiceNotFoundException;

}
