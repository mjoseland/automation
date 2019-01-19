package name.joseland.mal.automation.requestrepo.db;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import name.joseland.mal.automation.core.GenericEntityTester;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class InternalRequestTest {

    static final HttpMethod TEST_HTTP_METHOD = HttpMethod.GET;
    static final HttpMethod UPDATE_TEST_HTTP_METHOD = HttpMethod.PUT;

    static final String TEST_SERVICE_ID = "scheduler";
    static final String UPDATE_TEST_SERVICE_ID = "text-source-monitor";

    static final String TEST_RESOURCE = "/trigger-configs/10";
    static final String UPDATE_TEST_RESOURCE = "/http-sources/11/perform-monitor";

    static final JsonNode TEST_BODY = BooleanNode.TRUE;
    static final JsonNode UPDATE_TEST_BODY = BooleanNode.FALSE;


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

}
