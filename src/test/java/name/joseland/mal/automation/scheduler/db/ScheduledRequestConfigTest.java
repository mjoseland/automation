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
public class ScheduledRequestConfigTest {

    @Autowired
    private ScheduledRequestConfigRepository repository;

    @Autowired
    private TriggerConfigRepository triggerConfigRepository;

    private GenericEntityTester<ScheduledRequestConfig> tester;

    @Before
    public void init() {
        tester = GenericEntityTester.buildNew(ScheduledRequestConfig::new, repository);

        // create test TriggerConfig instance
        TriggerConfig testTriggerConfig = new TriggerConfig();
        testTriggerConfig.setType(TriggerConfig.Type.CRON);
        testTriggerConfig.setCronExpression(CronTriggerConfigTest.TEST_CRON_EXPRESSION);
        triggerConfigRepository.save(testTriggerConfig);

        // create update test TriggerConfig instance
        TriggerConfig updateTestTriggerConfig = new TriggerConfig();
        testTriggerConfig.setType(TriggerConfig.Type.CRON);
        updateTestTriggerConfig.setCronExpression(CronTriggerConfigTest.UPDATE_TEST_CRON_EXPRESSION);
        triggerConfigRepository.save(updateTestTriggerConfig);

        // provide testing field params for the triggerConfig field
        tester.addField(testTriggerConfig, updateTestTriggerConfig,
                ScheduledRequestConfig::getTriggerConfig, ScheduledRequestConfig::setTriggerConfig);

        // provide HTTP request repo links
        String requestRepositoryMapping = "/internal-requests/52/assemble";
        String updateRequestRepositoryMapping = "/internal-requests/45/assemble";

        tester.addField(requestRepositoryMapping, updateRequestRepositoryMapping,
                ScheduledRequestConfig::getRequestRepositoryMapping,
                ScheduledRequestConfig::setRequestRepositoryMapping);
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
