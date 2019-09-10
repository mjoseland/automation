package name.joseland.mal.automation.scheduler.db;

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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

@RunWith(SpringRunner.class)
@ContextConfiguration(initializers = { CronTriggerConfigTest.Initializer.class })
@DataJpaTest
public class CronTriggerConfigTest {

    public static final TriggerConfig.Type TEST_TYPE = TriggerConfig.Type.CRON;
    public static final TriggerConfig.Type OTHER_TEST_TYPE = TriggerConfig.Type.CRON;

    public static final String TEST_CRON_EXPRESSION = "0 15 10 ? * *";
    public static final String OTHER_TEST_CRON_EXPRESSION = "10 21 01 ? * *";

    @ClassRule
    public static PostgreSQLContainer postgreSqlContainer = new PostgreSQLContainer("postgres:11.4");

    @Autowired
    private TriggerConfigRepository repository;

    private GenericEntityTester<TriggerConfig> tester;

    @Before
    public void init() {
        tester = GenericEntityTester.buildNew(TriggerConfig::new, repository);

        // add type field
        tester.addField(TEST_TYPE, OTHER_TEST_TYPE, TriggerConfig::getType, TriggerConfig::setType);

        // add cron expression field
        tester.addField(TEST_CRON_EXPRESSION, OTHER_TEST_CRON_EXPRESSION,
                TriggerConfig::getCronExpression, TriggerConfig::setCronExpression);
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
