package name.joseland.mal.automation.requestrepo.db;

import name.joseland.mal.automation.core.GenericEntityTester;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@ContextConfiguration(initializers = ExternalRequestTest.Initializer.class)
@DataJpaTest
public class ExternalRequestTest {

	public static final String TEST_DESCRIPTION = "test description";
	public static final String OTHER_TEST_DESCRIPTION = "other test description";

	public static final HttpMethod TEST_HTTP_METHOD = HttpMethod.GET;
	public static final HttpMethod OTHER_TEST_HTTP_METHOD = HttpMethod.PUT;

	public static final String TEST_URL = "http://test.com/resource";
	public static final String OTHER_TEST_URL = "http://other-url.com/other-resource";

	public static final Map<String, String> TEST_HEADER_FIELDS = new HashMap<>();
	public static final Map<String, String> OTHER_TEST_HEADER_FIELDS = new HashMap<>();

	public static final String TEST_BODY = "test body";
	public static final String OTHER_TEST_BODY = "other test body";

	static {
		TEST_HEADER_FIELDS.put("test_key", "test_value");
		TEST_HEADER_FIELDS.put("test_key2", "test_value2");

		OTHER_TEST_HEADER_FIELDS.put("other_test_key", "other_test_value");
		OTHER_TEST_HEADER_FIELDS.put("other_test_key2", "other_test_value2");
	}


	@ClassRule
	public static PostgreSQLContainer postgreSqlContainer =
			new PostgreSQLContainer("postgres:11.4");

	@Autowired
	private ExternalRequestRepository repository;

	private GenericEntityTester<ExternalRequest, Integer> tester;


	@Before
	public void init() {
		GenericEntityTester<ExternalRequest, Integer> tester =
				GenericEntityTester.buildNew(ExternalRequest::new, repository);

		tester.addField(TEST_DESCRIPTION, OTHER_TEST_DESCRIPTION, ExternalRequest::getDescription,
				ExternalRequest::setDescription);
		tester.addField(TEST_HTTP_METHOD, OTHER_TEST_HTTP_METHOD, ExternalRequest::getHttpMethod,
				ExternalRequest::setHttpMethod);
		tester.addField(TEST_URL, OTHER_TEST_URL, ExternalRequest::getUrl,
				ExternalRequest::setUrl);
		tester.addField(TEST_HEADER_FIELDS, OTHER_TEST_HEADER_FIELDS, ExternalRequest::getHeaderFields,
				ExternalRequest::setHeaderFields);
		tester.addField(TEST_BODY, OTHER_TEST_BODY, ExternalRequest::getBody,
				ExternalRequest::setBody);

		this.tester = tester;
	}

	@Test
	public void saveRetrieve() {
		tester.saveRetrieve();
	}

	@Test
	public void delete() {
		tester.delete();
	}

	@Test
	public void update() {
		tester.update();
	}

	@Test
	public void testDescriptionNonNull() {
		assertThrows(DataIntegrityViolationException.class,
				() -> tester.saveNewInstanceWithSetterApplied(ExternalRequest::setDescription, null));
	}

	@Test
	public void testHttpMethodNonNull() {
		assertThrows(DataIntegrityViolationException.class,
				() -> tester.saveNewInstanceWithSetterApplied(ExternalRequest::setHttpMethod, null));
	}

	@Test
	public void testUrlNonNull() {
		assertThrows(DataIntegrityViolationException.class,
				() -> tester.saveNewInstanceWithSetterApplied(ExternalRequest::setUrl, null));
	}


	static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
		@Override
		public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
			TestPropertyValues.of(
					"spring.datasource.url=" + postgreSqlContainer.getJdbcUrl(),
					"spring.datasource.username=" + postgreSqlContainer.getUsername(),
					"spring.datasource.password=" + postgreSqlContainer.getPassword()
			).applyTo(configurableApplicationContext.getEnvironment());
		}
	}

}
