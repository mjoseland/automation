package name.joseland.mal.automation.requestrepo.db;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
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

@RunWith(SpringRunner.class)
@ContextConfiguration(initializers = {InternalRequestTest.Initializer.class})
@DataJpaTest
public class InternalRequestTest {

    private static final HttpMethod TEST_HTTP_METHOD = HttpMethod.GET;
    private static final HttpMethod UPDATE_TEST_HTTP_METHOD = HttpMethod.PUT;

    private static final String TEST_SERVICE_ID = "scheduler";
    private static final String UPDATE_TEST_SERVICE_ID = "text-source-monitor";

    private static final String TEST_RESOURCE = "/trigger-configs/10";
    private static final String UPDATE_TEST_RESOURCE = "/http-sources/11/perform-monitor";

    private static final JsonNode TEST_BODY = BooleanNode.TRUE;
    private static final JsonNode UPDATE_TEST_BODY = BooleanNode.FALSE;


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

        tester.addField(TEST_HTTP_METHOD, UPDATE_TEST_HTTP_METHOD, InternalRequest::getHttpMethod,
                InternalRequest::setHttpMethod);
        tester.addField(TEST_SERVICE_ID, UPDATE_TEST_SERVICE_ID, InternalRequest::getServiceId,
                InternalRequest::setServiceId);
        tester.addField(TEST_RESOURCE, UPDATE_TEST_RESOURCE, InternalRequest::getResource,
                InternalRequest::setResource);
        tester.addField(TEST_BODY, UPDATE_TEST_BODY, InternalRequest::getBody,
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
