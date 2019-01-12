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
public class CronExpressionTest {

    static final String DESCRIPTION = "www.example.com/testobj/123";
    static final String DESCRIPTION_UPDATE = "www.example.com/testobj/333";
    static final String EXPRESSION = "GET";
    static final String EXPRESSION_UPDATE = "DELETE";

    @Autowired
    private CronExpressionRepository repository;

    private GenericEntityTester<CronExpression> tester;

    @Before
    public void init() {
        tester = GenericEntityTester.buildNew(CronExpression::new, repository);

        tester.addField(DESCRIPTION, DESCRIPTION_UPDATE, CronExpression::getDescription,
                CronExpression::setDescription);

        tester.addField(EXPRESSION, EXPRESSION_UPDATE, CronExpression::getExpression,
                CronExpression::setExpression);
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
