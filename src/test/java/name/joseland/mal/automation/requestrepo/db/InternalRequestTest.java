package name.joseland.mal.automation.requestrepo.db;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.IOException;

@RunWith(SpringRunner.class)
@ContextConfiguration(initializers = {InternalRequestTest.Initializer.class})
@DataJpaTest
public class InternalRequestTest {

    public static final HttpMethod TEST_HTTP_METHOD = HttpMethod.GET;
    public static final HttpMethod OTHER_TEST_HTTP_METHOD = HttpMethod.PUT;

    public static final String TEST_SERVICE_ID = "scheduler";
    public static final String OTHER_TEST_SERVICE_ID = "text-source-monitor";

    public static final String TEST_RESOURCE = "/trigger-configs/10";
    public static final String OTHER_TEST_RESOURCE = "/http-sources/11/perform-monitor";

    public static final JsonNode TEST_BODY;
    public static final JsonNode OTHER_TEST_BODY;

    static {
        JsonNode testBodyNode;
        JsonNode otherTestBodyNode;
        try {
            testBodyNode = new ObjectMapper().readTree("{ \"genericField\": 1 }");
            otherTestBodyNode = new ObjectMapper().readTree("{ \"genericField\": 2 }");
        } catch (IOException e) {
            e.printStackTrace();
            testBodyNode = null;
            otherTestBodyNode = null;
        }
        TEST_BODY = testBodyNode;
        OTHER_TEST_BODY = otherTestBodyNode;
    }


    @ClassRule
    public static PostgreSQLContainer postgreSqlContainer =
            new PostgreSQLContainer("postgres:11.4");

    @Autowired
    private InternalRequestRepository repository;

    private GenericEntityTester<InternalRequest> tester;


    @Before
    public void init() {
        GenericEntityTester<InternalRequest> tester = GenericEntityTester.buildNew(InternalRequest::new,
                repository);

        tester.addField(TEST_HTTP_METHOD, OTHER_TEST_HTTP_METHOD, InternalRequest::getHttpMethod,
                InternalRequest::setHttpMethod);
        tester.addField(TEST_SERVICE_ID, OTHER_TEST_SERVICE_ID, InternalRequest::getServiceId,
                InternalRequest::setServiceId);
        tester.addField(TEST_RESOURCE, OTHER_TEST_RESOURCE, InternalRequest::getResource,
                InternalRequest::setResource);
        tester.addField(TEST_BODY, OTHER_TEST_BODY, InternalRequest::getBody,
                InternalRequest::setBody);

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
