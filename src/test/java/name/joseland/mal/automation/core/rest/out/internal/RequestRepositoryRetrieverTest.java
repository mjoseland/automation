package name.joseland.mal.automation.core.rest.out.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import name.joseland.mal.automation.core.rest.out.internal.exception.InternalServiceNotFoundException;
import name.joseland.mal.automation.core.rest.out.internal.exception.InvalidInternalServiceRequestParameter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.util.Scanner;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.givenThat;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class RequestRepositoryRetrieverTest {

	private static final int PORT = 8082;
	// hardcoded so the unit test isn't reliant on config
	private static final String REQUEST_REPOSITORY_ID = "request-repository";
	private static final String REQUEST_REPOSITORY_CONTEXT_PATH = "/request-repository";
	private static final String ASSEMBLED_REQUEST_NOUN = "/assembled";
	private static final int INTERNAL_REQUEST_ID = 99;

	private final static String INTERNAL_REQUEST_RESOURCE_LINK = "/internal-requests/" + INTERNAL_REQUEST_ID;
	private static final String INTERNAL_REQUEST_RESOURCE_PATH = REQUEST_REPOSITORY_CONTEXT_PATH +
			INTERNAL_REQUEST_RESOURCE_LINK + ASSEMBLED_REQUEST_NOUN;

	private static final String REQUEST_BODY_JSON_FILE_NAME =
			"src/test/resources/json/request_repository_retriever_internal_request.json";

	// see RequestRepositoryRetriever::internalRequestFromJsonNode
	private static JsonNode internalRequestAsJson;

	// to be configured so it returns the request config stored at REQUEST_BODY_JSON_FILE_NAME when a GET is sent to
	// RESOURCE_PATH
	private static WireMockServer wireMockServer;


	@BeforeClass
	public static void beforeClass() throws IOException {
		// build and start wireMockServer
		wireMockServer = new WireMockServer(PORT);
		wireMockServer.start();
		configureFor("localhost", PORT);

		File file = new File(REQUEST_BODY_JSON_FILE_NAME);
		Scanner sc = new Scanner(file).useDelimiter("\\Z");
		String internalRequestAsStr = sc.next();
		sc.close();
		internalRequestAsJson = new ObjectMapper().readTree(internalRequestAsStr);
	}

	@AfterClass
	public static void afterClass() {
		wireMockServer.stop();
	}


	/* ********************************************************************************************************** */
	/* ************************************************** TESTS ************************************************* */
	/* ********************************************************************************************************** */


	@Test
	public void retrieveInternalRequestTest() throws InternalServiceNotFoundException, InterruptedException,
			InvalidInternalServiceRequestParameter, IOException, URISyntaxException {
		// create a WireMock stub that:
		//  - is available at URL localhost:[PORT][INTERNAL_REQUEST_RESOURCE_PATH]
		//  - returns response with status=200, Content-Type=application/json, body=JSON at REQUEST_BODY_JSON_FILE_NAME
		givenThat(get(urlEqualTo(INTERNAL_REQUEST_RESOURCE_PATH))
				.willReturn(aResponse()
						.withStatus(HttpStatus.OK.value())
						.withHeader("Content-Type", MediaType.APPLICATION_JSON)
						.withBody(internalRequestAsJson.toString())));

		// create the URI that will be returned by the mocked InternalRequestUriAssembler
		// this URI points to the wiremock server initialised above
		URI requestRepositoryUri = URI.create(wireMockServer.baseUrl() + INTERNAL_REQUEST_RESOURCE_PATH);

		// mock the InternalRequestUriAssembler. returns requestRepositoryId when fromResourcePath is called with
		// the arguments: (REQUEST_REPOSITORY_ID, INTERNAL_REQUEST_RESOURCE_PATH)
		InternalRequestUriAssembler mockInternalRequestUriAssembler = Mockito.mock(InternalRequestUriAssembler.class);
		when(mockInternalRequestUriAssembler.fromResourcePath(REQUEST_REPOSITORY_ID, INTERNAL_REQUEST_RESOURCE_PATH))
				.thenReturn(requestRepositoryUri);

		// build the RequestRepositoryRetriever
		RequestRepositoryRetriever requestRepositoryRetriever = new RequestRepositoryRetriever(REQUEST_REPOSITORY_ID,
				REQUEST_REPOSITORY_CONTEXT_PATH, ASSEMBLED_REQUEST_NOUN, mockInternalRequestUriAssembler);

		// retrieve the internal request
		HttpRequest retrievedInternalRequest =
				requestRepositoryRetriever.getConstructedRequest(INTERNAL_REQUEST_RESOURCE_LINK);

		assertEquals(internalRequestAsJson.get("httpMethod").asText(), retrievedInternalRequest.method());
		assertEquals(internalRequestAsJson.get("url").asText(), retrievedInternalRequest.uri().toString());

		verify(getRequestedFor(urlMatching(INTERNAL_REQUEST_RESOURCE_PATH))
				.withHeader("Accept", matching(MediaType.APPLICATION_JSON)));
	}


}

