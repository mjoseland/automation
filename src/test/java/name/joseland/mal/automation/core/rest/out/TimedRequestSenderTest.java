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

    private static int port = 8082;

    private static int status = HttpStatus.OK.value();
    private static String jsonTypeStr = MediaType.APPLICATION_JSON;
    private static String stubUrl = "/test/resource";
    private static JsonNode requestBodyJsonNode = null;
    private static JsonNode responseBodyJsonNode = null;

    private static WireMockServer wireMockServer;

    @BeforeClass
    public static void startServer() {
        wireMockServer = new WireMockServer(port);
        wireMockServer.start();
        configureFor("localhost", port);

        // initialise the test request and response bodies
        try {
            requestBodyJsonNode = new ObjectMapper().readTree("{ \"requestParam\": \"generic_string\"}");
            responseBodyJsonNode = new ObjectMapper().readTree("{ \"success\": true }");
        } catch (IOException e) {
            e.printStackTrace();
            requestBodyJsonNode = responseBodyJsonNode = null;
        }
    }

    @AfterClass
    public static void stopServer() {
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
        //  - is available at URL localhost:8080/my/resource
        //  - accepts JSON
        //  - returns response with status=200, Content-Type=application/json, body={"success": true}
        givenThat(put(urlEqualTo(stubUrl))
                .willReturn(aResponse()
                        .withStatus(status)
                        .withHeader("Content-Type", jsonTypeStr)
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
        assertEquals(status, response.statusCode());
        // assert that the time the request was sent is before the time the response ras recieved
        assertTrue(response.getTimeRequested().isBefore(response.getTimeReceived()));

        // verify that the request was received by the stub
        // on failure, throws com.github.tomakehurst.wiremock.client.VerificationException
        verify(putRequestedFor(urlMatching(stubUrl))
                .withRequestBody(matching(".*" + requestBodyJsonNode.asText() + ".*"))
                .withHeader("Content-Type", matching(jsonTypeStr)));
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
                .uri(URI.create(wireMockServer.baseUrl() + stubUrl))
                .PUT(HttpRequest.BodyPublishers.ofString(requestBodyJsonNode.asText()))
                .header("Content-Type", jsonTypeStr)
                .build();
    }

}
