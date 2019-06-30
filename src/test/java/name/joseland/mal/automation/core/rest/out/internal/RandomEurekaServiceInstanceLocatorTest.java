package name.joseland.mal.automation.core.rest.out.internal;

import name.joseland.mal.automation.core.rest.out.internal.exception.InternalServiceNotFoundException;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class RandomEurekaServiceInstanceLocatorTest {

	@Test
	public void findInstanceTwoInstancesTest() throws InternalServiceNotFoundException {
		String resourceId = "testResource";

		// mock two ServiceInstance objects, add them to a list
		ServiceInstance firstMockServiceInstance = Mockito.mock(ServiceInstance.class);
		ServiceInstance secondMockServiceInstance = Mockito.mock(ServiceInstance.class);
		List<ServiceInstance> mockServiceInstanceList = new ArrayList<>();
		mockServiceInstanceList.add(firstMockServiceInstance);
		mockServiceInstanceList.add(secondMockServiceInstance);

		assertNotEquals(firstMockServiceInstance, secondMockServiceInstance);

		// mock a DiscoveryClient
		DiscoveryClient mockDiscoveryClient = Mockito.mock(DiscoveryClient.class);
		when(mockDiscoveryClient.getInstances(resourceId)).thenReturn(mockServiceInstanceList);

		// create the RandomEurekaServiceInstanceLocator
		RandomEurekaServiceInstanceLocator serviceInstanceLocator =
				new RandomEurekaServiceInstanceLocator(mockDiscoveryClient);

		// assert that one of the mocked service instances can be found
		ServiceInstance foundInstance = serviceInstanceLocator.findInstance(resourceId);
		boolean foundInstanceIsAMockedInstance =
				foundInstance.equals(firstMockServiceInstance) || foundInstance.equals(secondMockServiceInstance);
		assertTrue("found instance is not equal to one of the mocked instances",
				foundInstanceIsAMockedInstance);
	}

	@Test
	public void findInstanceOneInstanceTest() throws InternalServiceNotFoundException {
		String resourceId = "testResource";

		// mock a ServiceInstance object, add it to a list
		ServiceInstance firstMockServiceInstance = Mockito.mock(ServiceInstance.class);
		List<ServiceInstance> mockServiceInstanceList = new ArrayList<>();
		mockServiceInstanceList.add(firstMockServiceInstance);

		// mock a DiscoveryClient that returns the list with the mocked ServiceInstance
		DiscoveryClient mockDiscoveryClient = Mockito.mock(DiscoveryClient.class);
		when(mockDiscoveryClient.getInstances(resourceId)).thenReturn(mockServiceInstanceList);

		// create the RandomEurekaServiceInstanceLocator
		RandomEurekaServiceInstanceLocator serviceInstanceLocator =
				new RandomEurekaServiceInstanceLocator(mockDiscoveryClient);

		// assert that the found ServiceInstance for resourceId is equal to the mocked instance
		ServiceInstance foundInstance = serviceInstanceLocator.findInstance(resourceId);
		assertEquals(firstMockServiceInstance, foundInstance);
	}

	@Test(expected = InternalServiceNotFoundException.class)
	public void findInstanceNoInstancesTest() throws InternalServiceNotFoundException {
		// mock a DiscoveryClient
		DiscoveryClient mockDiscoveryClient = Mockito.mock(DiscoveryClient.class);

		// create the RandomEurekaServiceInstanceLocator
		RandomEurekaServiceInstanceLocator serviceInstanceLocator =
				new RandomEurekaServiceInstanceLocator(mockDiscoveryClient);

		// assert that the found ServiceInstance for resourceId is equal to the mocked instance
		serviceInstanceLocator.findInstance("failingResourceId");
	}

}
