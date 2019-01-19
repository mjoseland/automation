package name.joseland.mal.automation.scheduler.db;

import name.joseland.mal.automation.core.GenericEntityTester;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CronTriggerConfigTest {

    static final String TEST_CRON_EXPRESSION = "0 15 10 ? * *";
    static final String UPDATE_TEST_CRON_EXPRESSION = "10 21 01 ? * *";

    @Autowired
    private TriggerConfigRepository repository;

    private GenericEntityTester<TriggerConfig> tester;

    @Before
    public void init() {
        tester = GenericEntityTester.buildNew(TriggerConfig::new, repository);

        // provide testing field params for the cronExpression field
        tester.addField(TEST_CRON_EXPRESSION, UPDATE_TEST_CRON_EXPRESSION,
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

}
