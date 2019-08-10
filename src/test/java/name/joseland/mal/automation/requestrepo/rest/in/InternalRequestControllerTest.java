package name.joseland.mal.automation.requestrepo.rest.in;

import name.joseland.mal.automation.core.GenericRestControllerTester;
import name.joseland.mal.automation.core.rest.out.internal.HttpRequestDto;
import name.joseland.mal.automation.requestrepo.HttpRequestDtoBuilder;
import name.joseland.mal.automation.requestrepo.db.InternalRequest;
import name.joseland.mal.automation.requestrepo.db.InternalRequestRepository;
import name.joseland.mal.automation.requestrepo.db.InternalRequestTest;
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

@WebMvcTest(InternalRequestController.class)
@Import(InternalRequestAssembler.class)
@RunWith(SpringRunner.class)
public class InternalRequestControllerTest {

	private static final String RETRIEVE_ALL_RESPONSE_BODY_FILE_NAME =
			"src/test/resources/json/requestrepo/internal_request_retrieve_all_response.json";

	private static final String CREATE_REQUEST_BODY_FILE_NAME =
			"src/test/resources/json/requestrepo/internal_request_create_request.json";
	// also used for retrieveTest
	private static final String CREATE_RESPONSE_BODY_FILE_NAME =
			"src/test/resources/json/requestrepo/internal_request_create_response.json";

	private static final String RETRIEVE_HTTP_REQUEST_DTO_RESPONSE_BODY_FILE_NAME =
			"src/test/resources/json/requestrepo/internal_request_retrieve_http_request_dto_response.json";

	private static final String UPDATE_REQUEST_BODY_FILE_NAME =
			"src/test/resources/json/requestrepo/internal_request_update_request.json";
	private static final String UPDATE_RESPONSE_BODY_FILE_NAME =
			"src/test/resources/json/requestrepo/internal_request_update_response.json";

	private final GenericRestControllerTester<InternalRequest, Integer> restControllerTester;

	@MockBean
	private InternalRequestRepository repository;

	@MockBean
	private HttpRequestDtoBuilder httpRequestDtoBuilder;

	@Autowired
	private MockMvc mockMvc;

	public InternalRequestControllerTest() {
		this.restControllerTester = new GenericRestControllerTester<>("/internal-requests",
				InternalRequest::getId);
	}


	/* ********************************************************************************************************** */
	/* ************************************************** TESTS ************************************************* */
	/* ********************************************************************************************************** */


	@Test
	public void retrieveAllTest() throws Exception {
		List<InternalRequest> requestsToAddToRepository = new ArrayList<>();
		requestsToAddToRepository.add(getTestInternalRequest());
		requestsToAddToRepository.add(getAlternativeTestInternalRequest());

		restControllerTester.performRetrieveAllTest(mockMvc, repository, RETRIEVE_ALL_RESPONSE_BODY_FILE_NAME,
				requestsToAddToRepository);
	}

	@Test
	public void createTest() throws Exception {
		InternalRequest unsavedInternalRequest = getTestInternalRequest();
		unsavedInternalRequest.setId(null);
		InternalRequest savedInternalRequest = getTestInternalRequest();

		restControllerTester.performCreateTest(mockMvc, repository, CREATE_REQUEST_BODY_FILE_NAME,
				CREATE_RESPONSE_BODY_FILE_NAME, unsavedInternalRequest, savedInternalRequest);
	}

	@Test
	public void retrieveTest() throws Exception {
		InternalRequest testInternalRequest = getTestInternalRequest();

		restControllerTester.performRetrieveTest(mockMvc, repository, CREATE_RESPONSE_BODY_FILE_NAME,
				testInternalRequest);
	}

	@Test
	public void retrieveHttpRequestDtoTest() throws Exception {
		InternalRequest testInternalRequest = getTestInternalRequest();

		// mock repository.findById(..)
		given(repository.findById(testInternalRequest.getId())).willReturn(Optional.of(testInternalRequest));

		given(httpRequestDtoBuilder.fromInternalRequest(testInternalRequest)).willReturn(getTestHttpRequestDto());

		String urlTemplate = restControllerTester.getContextPath() + "/" + testInternalRequest.getId() +
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
		InternalRequest originalEntityInstance = getTestInternalRequest();
		InternalRequest expectedEntityInstance = getAlternativeTestInternalRequest();
		expectedEntityInstance.setId(originalEntityInstance.getId());

		restControllerTester.performUpdateTest(mockMvc, repository, UPDATE_REQUEST_BODY_FILE_NAME,
				UPDATE_RESPONSE_BODY_FILE_NAME, originalEntityInstance, expectedEntityInstance);
	}

	@Test
	public void deleteTest() throws Exception {
		InternalRequest entityInstance = getTestInternalRequest();

		restControllerTester.performDeleteRequest(mockMvc, repository, entityInstance);
	}


	/* ********************************************************************************************************** */
	/* ********************************************* PRIVATE METHODS ******************************************** */
	/* ********************************************************************************************************** */


	private InternalRequest getTestInternalRequest() {
		InternalRequest internalRequest = new InternalRequest();
		internalRequest.setId(1);
		internalRequest.setDescription(InternalRequestTest.TEST_DESCRIPTION);
		internalRequest.setHttpMethod(InternalRequestTest.TEST_HTTP_METHOD);
		internalRequest.setServiceId(InternalRequestTest.TEST_SERVICE_ID);
		internalRequest.setResource(InternalRequestTest.TEST_RESOURCE);
		internalRequest.setBody(InternalRequestTest.TEST_BODY);

		return internalRequest;
	}

	private InternalRequest getAlternativeTestInternalRequest() {
		InternalRequest internalRequest = new InternalRequest();
		internalRequest.setId(2);
		internalRequest.setDescription(InternalRequestTest.OTHER_TEST_DESCRIPTION);
		internalRequest.setHttpMethod(InternalRequestTest.OTHER_TEST_HTTP_METHOD);
		internalRequest.setServiceId(InternalRequestTest.OTHER_TEST_SERVICE_ID);
		internalRequest.setResource(InternalRequestTest.OTHER_TEST_RESOURCE);
		internalRequest.setBody(InternalRequestTest.OTHER_TEST_BODY);

		return internalRequest;
	}

	private HttpRequestDto getTestHttpRequestDto() {
		return new HttpRequestDto(
				InternalRequestTest.TEST_HTTP_METHOD,
				"localhost:8088/scheduler" + InternalRequestTest.TEST_RESOURCE,
				new HashMap<>(),
				InternalRequestTest.TEST_BODY.toString());
	}

}
