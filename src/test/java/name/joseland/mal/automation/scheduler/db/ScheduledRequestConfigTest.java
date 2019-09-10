package name.joseland.mal.automation.scheduler.db;

import name.joseland.mal.automation.core.GenericEntityTester;
import name.joseland.mal.automation.core.rest.out.internal.StoredRequestType;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

@RunWith(SpringRunner.class)
@ContextConfiguration(initializers = { ScheduledRequestConfigTest.Initializer.class })
@DataJpaTest
public class ScheduledRequestConfigTest {

    public static final StoredRequestType TEST_REQUEST_TYPE = StoredRequestType.INTERNAL;
    public static final StoredRequestType OTHER_TEST_REQUEST_TYPE = StoredRequestType.EXTERNAL;

    public static final int TEST_REQUEST_ID = 1;
    public static final int OTHER_TEST_REQUEST_ID = 2;

    @ClassRule
    public static PostgreSQLContainer postgreSqlContainer = new PostgreSQLContainer("postgres:11.4");

    @Autowired
    private TriggerConfigRepository triggerConfigRepository;

    @Autowired
    private ScheduledRequestConfigRepository repository;

    private GenericEntityTester<ScheduledRequestConfig> tester;

    @Before
    public void init() {
        tester = GenericEntityTester.buildNew(ScheduledRequestConfig::new, repository);

        // create test TriggerConfig instance
        TriggerConfig testTriggerConfig = new TriggerConfig();
        testTriggerConfig.setType(CronTriggerConfigTest.TEST_TYPE);
        testTriggerConfig.setCronExpression(CronTriggerConfigTest.TEST_CRON_EXPRESSION);
        triggerConfigRepository.save(testTriggerConfig);

        // create update test TriggerConfig instance
        TriggerConfig updateTestTriggerConfig = new TriggerConfig();
        updateTestTriggerConfig.setType(CronTriggerConfigTest.OTHER_TEST_TYPE);
        updateTestTriggerConfig.setCronExpression(CronTriggerConfigTest.OTHER_TEST_CRON_EXPRESSION);
        triggerConfigRepository.save(updateTestTriggerConfig);

        // add trigger config field
        tester.addField(testTriggerConfig, updateTestTriggerConfig,
                ScheduledRequestConfig::getTriggerConfig, ScheduledRequestConfig::setTriggerConfig);

        // add request type field
        tester.addField(TEST_REQUEST_TYPE, OTHER_TEST_REQUEST_TYPE,
                ScheduledRequestConfig::getRequestType, ScheduledRequestConfig::setRequestType);

        // add request ID field
        tester.addField(TEST_REQUEST_ID, OTHER_TEST_REQUEST_ID,
                ScheduledRequestConfig::getRequestId, ScheduledRequestConfig::setRequestId);
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
