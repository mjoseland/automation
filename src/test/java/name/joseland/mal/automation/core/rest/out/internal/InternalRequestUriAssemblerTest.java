package name.joseland.mal.automation.core.rest.out.internal;

import name.joseland.mal.automation.core.rest.out.internal.exception.InternalServiceNotFoundException;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.cloud.client.ServiceInstance;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class InternalRequestUriAssemblerTest {

	@Test
	public void testFromResourcePath() throws InternalServiceNotFoundException, URISyntaxException {
		String testResourceId = "mockRepositoryId";
		String testServiceUriString = "https://www.test.com";
		String testResourcePath = "/books/5";

		// mock a URI
		URI mockUri = Mockito.mock(URI.class);
		when(mockUri.toString()).thenReturn(testServiceUriString);

		// mock a ServiceInstance that finds the mock URI
		ServiceInstance mockServiceInstance = Mockito.mock(ServiceInstance.class);
		when(mockServiceInstance.getUri()).thenReturn(mockUri);

		// mock a ServiceInstanceLocator that finds the serviceInstance from testResourceId
		ServiceInstanceLocator mockServiceInstanceLocator = Mockito.mock(ServiceInstanceLocator.class);
		when(mockServiceInstanceLocator.findInstance(testResourceId)).thenReturn(mockServiceInstance);

		// initialise the InternalRequestUriAssembler
		InternalRequestUriAssembler internalRequestUriAssembler =
				new InternalRequestUriAssembler(mockServiceInstanceLocator);

		// test fromResourcePath
		URI assembledUri = internalRequestUriAssembler.fromResourcePath(testResourceId, testResourcePath);

		assertEquals(testServiceUriString + testResourcePath, assembledUri.toString());
	}

}
