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

    @Autowired
    private CronTriggerConfigRepository repository;

    @Autowired
    private CronExpressionRepository cronExpressionRepository;

    private GenericEntityTester<CronTriggerConfig> tester;

    @Before
    public void init() {
        tester = GenericEntityTester.buildNew(CronTriggerConfig::new, repository);

        // create test CronExpression instance
        CronExpression testCronExpression = new CronExpression();
        testCronExpression.setDescription(CronExpressionTest.DESCRIPTION);
        testCronExpression.setExpression(CronExpressionTest.EXPRESSION);
        cronExpressionRepository.save(testCronExpression);

        // create updateTest CronExpression instance
        CronExpression updateTestCronExpression = new CronExpression();
        updateTestCronExpression.setDescription(CronExpressionTest.DESCRIPTION_UPDATE);
        updateTestCronExpression.setExpression(CronExpressionTest.EXPRESSION_UPDATE);
        cronExpressionRepository.save(updateTestCronExpression);

        // provide testing field params for the cronExpression field
        tester.addField(testCronExpression, updateTestCronExpression,
                CronTriggerConfig::getCronExpression, CronTriggerConfig::setCronExpression);
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
