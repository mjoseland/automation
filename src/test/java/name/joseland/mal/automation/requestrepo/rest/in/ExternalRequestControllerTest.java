package name.joseland.mal.automation.requestrepo.rest.in;

import name.joseland.mal.automation.core.GenericRestControllerTester;
import name.joseland.mal.automation.core.rest.out.internal.HttpRequestDto;
import name.joseland.mal.automation.requestrepo.HttpRequestDtoBuilder;
import name.joseland.mal.automation.requestrepo.db.ExternalRequest;
import name.joseland.mal.automation.requestrepo.db.ExternalRequestRepository;
import name.joseland.mal.automation.requestrepo.db.ExternalRequestTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExternalRequestController.class)
@Import(ExternalRequestAssembler.class)
@RunWith(SpringRunner.class)
public class ExternalRequestControllerTest {


	private static final String RETRIEVE_ALL_RESPONSE_BODY_FILE_NAME =
			"src/test/resources/json/requestrepo/external_request_retrieve_all_response.json";

	private static final String CREATE_REQUEST_BODY_FILE_NAME =
			"src/test/resources/json/requestrepo/external_request_create_request.json";
	// also used for retrieveTest
	private static final String CREATE_RESPONSE_BODY_FILE_NAME =
			"src/test/resources/json/requestrepo/external_request_create_response.json";

	private static final String RETRIEVE_HTTP_REQUEST_DTO_RESPONSE_BODY_FILE_NAME =
			"src/test/resources/json/requestrepo/external_request_retrieve_http_request_dto_response.json";

	private static final String UPDATE_REQUEST_BODY_FILE_NAME =
			"src/test/resources/json/requestrepo/external_request_update_request.json";
	private static final String UPDATE_RESPONSE_BODY_FILE_NAME =
			"src/test/resources/json/requestrepo/external_request_update_response.json";

	private final GenericRestControllerTester<ExternalRequest, Integer> restControllerTester;

	@MockBean
	private ExternalRequestRepository repository;

	@MockBean
	private HttpRequestDtoBuilder httpRequestDtoBuilder;

	@Autowired
	private MockMvc mockMvc;

	public ExternalRequestControllerTest() {
		this.restControllerTester = new GenericRestControllerTester<>("/external-requests",
				ExternalRequest::getId);
	}


	/* ********************************************************************************************************** */
	/* ************************************************** TESTS ************************************************* */
	/* ********************************************************************************************************** */


	@Test
	public void retrieveAllTest() throws Exception {
		List<ExternalRequest> requestsToAddToRepository = new ArrayList<>();
		requestsToAddToRepository.add(getTestExternalRequest());
		requestsToAddToRepository.add(getAlternativeTestExternalRequest());

		restControllerTester.performRetrieveAllTest(mockMvc, repository, RETRIEVE_ALL_RESPONSE_BODY_FILE_NAME,
				requestsToAddToRepository);
	}

	@Test
	public void createTest() throws Exception {
		ExternalRequest unsavedExternalRequest = getTestExternalRequest();
		unsavedExternalRequest.setId(null);
		ExternalRequest savedExternalRequest = getTestExternalRequest();

		restControllerTester.performCreateTest(mockMvc, repository, CREATE_REQUEST_BODY_FILE_NAME,
				CREATE_RESPONSE_BODY_FILE_NAME, unsavedExternalRequest, savedExternalRequest);
	}

	@Test
	public void retrieveTest() throws Exception {
		ExternalRequest testExternalRequest = getTestExternalRequest();

		restControllerTester.performRetrieveTest(mockMvc, repository, CREATE_RESPONSE_BODY_FILE_NAME,
				testExternalRequest);
	}

	@Test
	public void retrieveHttpRequestDtoTest() throws Exception {
		ExternalRequest testExternalRequest = getTestExternalRequest();

		// mock repository.findById(..)
		given(repository.findById(testExternalRequest.getId())).willReturn(Optional.of(testExternalRequest));

		given(httpRequestDtoBuilder.fromExternalRequest(testExternalRequest)).willReturn(getTestHttpRequestDto());

		String urlTemplate = restControllerTester.getContextPath() + "/" + testExternalRequest.getId() +
				"/http-request-dto";

		// expected response
		String expectedResponseBodyJsonStr =
				restControllerTester.retrieveJsonFileAsString(RETRIEVE_HTTP_REQUEST_DTO_RESPONSE_BODY_FILE_NAME);

		mockMvc.perform(get(urlTemplate)
				.accept(MediaTypes.HAL_JSON_UTF8))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8_VALUE))
				.andExpect(content().json(expectedResponseBodyJsonStr, true));
	}

	@Test
	public void updateTest() throws Exception {
		ExternalRequest originalEntityInstance = getTestExternalRequest();
		ExternalRequest expectedEntityInstance = getAlternativeTestExternalRequest();
		expectedEntityInstance.setId(originalEntityInstance.getId());

		restControllerTester.performUpdateTest(mockMvc, repository, UPDATE_REQUEST_BODY_FILE_NAME,
				UPDATE_RESPONSE_BODY_FILE_NAME, originalEntityInstance, expectedEntityInstance);
	}

	@Test
	public void deleteTest() throws Exception {
		ExternalRequest entityInstance = getTestExternalRequest();

		restControllerTester.performDeleteRequest(mockMvc, repository, entityInstance);
	}


	/* ********************************************************************************************************** */
	/* ********************************************* PRIVATE METHODS ******************************************** */
	/* ********************************************************************************************************** */


	private ExternalRequest getTestExternalRequest() {
		ExternalRequest externalRequest = new ExternalRequest();
		externalRequest.setId(1);
		externalRequest.setDescription(ExternalRequestTest.TEST_DESCRIPTION);
		externalRequest.setHttpMethod(ExternalRequestTest.TEST_HTTP_METHOD);
		externalRequest.setUrl(ExternalRequestTest.TEST_URL);
		externalRequest.setHeaderFields(new HashMap<>(ExternalRequestTest.TEST_HEADER_FIELDS));
		externalRequest.setBody(ExternalRequestTest.TEST_BODY);

		return externalRequest;
	}

	private ExternalRequest getAlternativeTestExternalRequest() {
		ExternalRequest externalRequest = new ExternalRequest();
		externalRequest.setId(2);
		externalRequest.setDescription(ExternalRequestTest.OTHER_TEST_DESCRIPTION);
		externalRequest.setHttpMethod(ExternalRequestTest.OTHER_TEST_HTTP_METHOD);
		externalRequest.setUrl(ExternalRequestTest.OTHER_TEST_URL);
		externalRequest.setHeaderFields(new HashMap<>(ExternalRequestTest.OTHER_TEST_HEADER_FIELDS));
		externalRequest.setBody(ExternalRequestTest.OTHER_TEST_BODY);

		return externalRequest;
	}

	private HttpRequestDto getTestHttpRequestDto() {
		return new HttpRequestDto(
				ExternalRequestTest.TEST_HTTP_METHOD,
				ExternalRequestTest.TEST_URL,
				ExternalRequestTest.TEST_HEADER_FIELDS,
				ExternalRequestTest.TEST_BODY);
	}

}
