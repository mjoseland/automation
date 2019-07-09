package name.joseland.mal.automation.core.rest.out;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TimedRequestSenderTest {

    private static final int PORT = 8082;
    private static final String STUB_URL = "/test/resource";

    private static final String REQUEST_BODY_JSON_STR = "{ \"requestParam\": \"generic_string\"}";
    private static final String RESPONSE_BODY_JSON_STR = "{ \"success\": true }";

    private static JsonNode requestBodyJsonNode = null;
    private static JsonNode responseBodyJsonNode = null;

    private static WireMockServer wireMockServer;

    @BeforeClass
    public static void beforeClass() throws IOException {
        // build and start wireMockServer
        wireMockServer = new WireMockServer(PORT);
        wireMockServer.start();
        configureFor("localhost", PORT);

        // initialise the test request and response bodies
		requestBodyJsonNode = new ObjectMapper().readTree(REQUEST_BODY_JSON_STR);
		responseBodyJsonNode = new ObjectMapper().readTree(RESPONSE_BODY_JSON_STR);
    }

    @AfterClass
    public static void afterClass() {
        wireMockServer.stop();
    }


    /* ********************************************************************************************************** */
    /* ************************************************** TESTS ************************************************* */
    /* ********************************************************************************************************** */


    @Test(expected = NullPointerException.class)
    public void buildFailsWithNullRequest() {
        TimedRequestSender.build(
                null,
                getStringBodyHandler());
    }

    @Test(expected = NullPointerException.class)
    public void buildFailsWithNullBodyHandler() {
        TimedRequestSender.build(
                getGenericHttpRequest(),
                null);
    }

    @Test
    public void buildTest() {
        TimedRequestSender requestSender = TimedRequestSender.build(
                getGenericHttpRequest(),
                getStringBodyHandler());

        assertNotNull(requestSender);
    }

    @Test
    public void putRequestTest() throws IOException, InterruptedException {
        // create the WireMock stub that:
        //  - is available at URL localhost:[PORT][STUB_URL]
        //  - returns response with status=200, Content-Type=application/json, body={"success": true}
        givenThat(put(urlEqualTo(STUB_URL))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                        .withBody(responseBodyJsonNode.asText())));

        // create the timed request sender that will send a a request:
        //  - to URL localhost:8080/my/resource
        //  - reads the response as a String
        TimedRequestSender<String> requestSender = TimedRequestSender.build(
                getCallTestHttpRequest(),
                getStringBodyHandler());
        // use the request sender to send the request
        TimedHttpResponse<String> response = requestSender.call();

        // assert that the response is equal to the WireMock response
        assertEquals(responseBodyJsonNode.asText(), response.body());
        assertEquals(HttpStatus.OK.value(), response.statusCode());
        // assert that the time the request was sent is before the time the response ras recieved
        assertTrue(response.getTimeRequested().isBefore(response.getTimeReceived()));

        // verify that the request was received by the stub
        // on failure, throws com.github.tomakehurst.wiremock.client.VerificationException
        verify(putRequestedFor(urlMatching(STUB_URL))
                .withRequestBody(matching(".*" + requestBodyJsonNode.asText() + ".*"))
                .withHeader("Content-Type", matching(MediaType.APPLICATION_JSON)));
    }


    /* ********************************************************************************************************** */
    /* ********************************************* PRIVATE METHODS ******************************************** */
    /* ********************************************************************************************************** */


    private static HttpRequest getGenericHttpRequest() {
        return HttpRequest.newBuilder()
                .uri(URI.create("http://test.com"))
                .build();
    }

    private static HttpResponse.BodyHandler<String> getStringBodyHandler() {
        return HttpResponse.BodyHandlers
                .ofString();
    }

    private static HttpRequest getCallTestHttpRequest() {
        return HttpRequest.newBuilder()
                .uri(URI.create(wireMockServer.baseUrl() + STUB_URL))
                .PUT(HttpRequest.BodyPublishers.ofString(requestBodyJsonNode.asText()))
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .build();
    }

}
