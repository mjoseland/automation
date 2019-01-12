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
public class HttpRequestConfigTest {

    static final String URL = "www.example.com/testobj/123";
    static final String URL_UPDATE = "www.example.com/testobj/333";
    static final String VERB = "GET";
    static final String VERB_UPDATE = "DELETE";
    static final String BODY = "{ }";
    static final String BODY_UPDATE = "{ a: \"b\" }";

    @Autowired
    private HttpRequestConfigRepository repository;

    private GenericEntityTester<HttpRequestConfig> tester;

    @Before
    public void init() {
        tester = GenericEntityTester.buildNew(HttpRequestConfig::new, repository);

        tester.addField(URL, URL_UPDATE, HttpRequestConfig::getUrl, HttpRequestConfig::setUrl);
        tester.addField(VERB, VERB_UPDATE, HttpRequestConfig::getVerb, HttpRequestConfig::setVerb);
        tester.addField(BODY, BODY_UPDATE, HttpRequestConfig::getBody, HttpRequestConfig::setBody);
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
