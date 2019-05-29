package name.joseland.mal.automation.core.rest.out.internal;

import name.joseland.mal.automation.core.rest.out.internal.exception.InternalServiceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

/**
 * Used to find services that are part of the automation application.
 *
 * Returns a randomly selected instance from those found by {@link DiscoveryClient}.
 */
@Component
@Scope(value = "singleton")
public class RandomEurekaServiceInstanceLocator implements ServiceInstanceLocator {

    private final DiscoveryClient discoveryClient;

    RandomEurekaServiceInstanceLocator(@Autowired DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    /**
     * Find a ServiceInstance for an internal service.
     *
     * Uses a {@link DiscoveryClient}.
     *
     * @param resourceId    the ID of the service to find
     * @return              the located service's {@link ServiceInstance}
     * @throws InternalServiceNotFoundException if the service couldn't be found
     */
    @Override
    public ServiceInstance findInstance(String resourceId) throws InternalServiceNotFoundException {
        List<ServiceInstance> serviceInstances = discoveryClient.getInstances(resourceId);

        if (serviceInstances.isEmpty())
            throw new InternalServiceNotFoundException(resourceId);

        if (serviceInstances.size() == 1)
            return serviceInstances.get(0);

        int randomIndex = new Random().nextInt(serviceInstances.size());

        return serviceInstances.get(randomIndex);
    }

}
