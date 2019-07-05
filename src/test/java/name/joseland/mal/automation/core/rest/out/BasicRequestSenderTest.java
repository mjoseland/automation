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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.givenThat;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BasicRequestSenderTest {

	private static final int PORT = 8082;
	private static final String STUB_URL = "/test/resource";

	private static JsonNode requestBodyJsonNode = null;
	private static JsonNode responseBodyJsonNode = null;

	private static WireMockServer wireMockServer;

	@BeforeClass
	public static void startServer() {
		wireMockServer = new WireMockServer(PORT);
		wireMockServer.start();
		configureFor("localhost", PORT);

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
		BasicRequestSender.build(
				null,
				getStringBodyHandler());
	}

	@Test(expected = NullPointerException.class)
	public void buildFailsWithNullBodyHandler() {
		BasicRequestSender.build(
				getGenericHttpRequest(),
				null);
	}

	@Test
	public void buildTest() {
		BasicRequestSender requestSender = BasicRequestSender.build(
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
		givenThat(put(urlEqualTo(STUB_URL))
				.willReturn(aResponse()
						.withStatus(HttpStatus.OK.value())
						.withHeader("Content-Type", MediaType.APPLICATION_JSON)
						.withBody(responseBodyJsonNode.asText())));

		// create the timed request sender that will send a a request:
		//  - to URL localhost:8080/my/resource
		//  - reads the response as a String
		BasicRequestSender<String> requestSender = BasicRequestSender.build(
				getCallTestHttpRequest(),
				getStringBodyHandler());
		// use the request sender to send the request
		HttpResponse<String> response = requestSender.call();

		// assert that the response is equal to the WireMock response
		assertEquals(responseBodyJsonNode.asText(), response.body());
		assertEquals(HttpStatus.OK.value(), response.statusCode());

		// verify that the request was recieved by the stub
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
