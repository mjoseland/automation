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
    private CronTriggerConfigRepository cronTriggerConfigRepository;
    @Autowired
    private CronExpressionRepository cronExpressionRepository;
    @Autowired
    private HttpRequestConfigRepository httpRequestConfigRepository;

    private GenericEntityTester<ScheduledRequestConfig> tester;

    @Before
    public void init() {
        tester = GenericEntityTester.buildNew(ScheduledRequestConfig::new, repository);

        // create test TriggerConfig instance
        CronExpression testCronExpression = new CronExpression();
        testCronExpression.setDescription(CronExpressionTest.DESCRIPTION);
        testCronExpression.setExpression(CronExpressionTest.EXPRESSION);
        cronExpressionRepository.save(testCronExpression);
        CronTriggerConfig testTriggerConfig = new CronTriggerConfig();
        testTriggerConfig.setCronExpression(testCronExpression);
        cronTriggerConfigRepository.save(testTriggerConfig);

        // create test TriggerConfig instance
        CronExpression updateTestCronExpression = new CronExpression();
        updateTestCronExpression.setDescription(CronExpressionTest.DESCRIPTION_UPDATE);
        updateTestCronExpression.setExpression(CronExpressionTest.EXPRESSION_UPDATE);
        cronExpressionRepository.save(updateTestCronExpression);
        CronTriggerConfig updateTestTriggerConfig = new CronTriggerConfig();
        updateTestTriggerConfig.setCronExpression(updateTestCronExpression);
        cronTriggerConfigRepository.save(updateTestTriggerConfig);

        // provide testing field params for the triggerConfig field
        tester.addField(testTriggerConfig, updateTestTriggerConfig,
                ScheduledRequestConfig::getTriggerConfig, ScheduledRequestConfig::setTriggerConfig);

        // create test HttpRequestConfig instance
        HttpRequestConfig testRequestConfig = new HttpRequestConfig();
        testRequestConfig.setUrl(HttpRequestConfigTest.URL);
        testRequestConfig.setVerb(HttpRequestConfigTest.VERB);
        testRequestConfig.setBody(HttpRequestConfigTest.BODY);
        httpRequestConfigRepository.save(testRequestConfig);

        // create test HttpRequestConfig instance
        HttpRequestConfig updateTestRequestConfig = new HttpRequestConfig();
        updateTestRequestConfig.setUrl(HttpRequestConfigTest.URL_UPDATE);
        updateTestRequestConfig.setVerb(HttpRequestConfigTest.VERB_UPDATE);
        updateTestRequestConfig.setBody(HttpRequestConfigTest.BODY_UPDATE);
        httpRequestConfigRepository.save(updateTestRequestConfig);

        // provide testing field params for the httpRequestConfig field
        tester.addField(testRequestConfig, updateTestRequestConfig,
                ScheduledRequestConfig::getHttpRequestConfig, ScheduledRequestConfig::setHttpRequestConfig);
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
