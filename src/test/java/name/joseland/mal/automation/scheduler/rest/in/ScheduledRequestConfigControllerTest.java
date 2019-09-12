package name.joseland.mal.automation.scheduler.rest.in;

import name.joseland.mal.automation.core.GenericRestControllerTester;
import name.joseland.mal.automation.scheduler.db.ScheduledRequestConfig;
import name.joseland.mal.automation.scheduler.db.ScheduledRequestConfigRepository;
import name.joseland.mal.automation.scheduler.db.ScheduledRequestConfigTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

@WebMvcTest(ScheduledRequestConfigController.class)
@Import(ScheduledRequestConfigAssembler.class)
@RunWith(SpringRunner.class)
public class ScheduledRequestConfigControllerTest {

	private static final String RETRIEVE_ALL_RESPONSE_BODY_FILE_NAME =
			"src/test/resources/json/scheduler/scheduled_request_config_retrieve_all_response.json";

	private static final String CREATE_REQUEST_BODY_FILE_NAME =
			"src/test/resources/json/scheduler/scheduled_request_config_create_request.json";
	// also used for retrieveTest
	private static final String CREATE_RESPONSE_BODY_FILE_NAME =
			"src/test/resources/json/scheduler/scheduled_request_config_create_response.json";

	private static final String UPDATE_REQUEST_BODY_FILE_NAME =
			"src/test/resources/json/scheduler/scheduled_request_config_update_request.json";
	private static final String UPDATE_RESPONSE_BODY_FILE_NAME =
			"src/test/resources/json/scheduler/scheduled_request_config_update_response.json";

	private final GenericRestControllerTester<ScheduledRequestConfig, Integer> restControllerTester;

	@MockBean
	private ScheduledRequestConfigRepository repository;

	@Autowired
	private MockMvc mockMvc;

	public ScheduledRequestConfigControllerTest() {
		this.restControllerTester = new GenericRestControllerTester<>("/scheduled-request-configs",
				ScheduledRequestConfig::getId);
	}


	/* ********************************************************************************************************** */
	/* ************************************************** TESTS ************************************************* */
	/* ********************************************************************************************************** */


	@Test
	public void retrieveAllTest() throws Exception {
		List<ScheduledRequestConfig> requestsToAddToRepository = new ArrayList<>();
		requestsToAddToRepository.add(getTestScheduledRequestConfig());
		requestsToAddToRepository.add(getAlternativeTestScheduledRequestConfig());

		restControllerTester.performRetrieveAllTest(mockMvc, repository, RETRIEVE_ALL_RESPONSE_BODY_FILE_NAME,
				requestsToAddToRepository);
	}

	@Test
	public void createTest() throws Exception {
		ScheduledRequestConfig unsavedScheduledRequestConfig = getTestScheduledRequestConfig();
		unsavedScheduledRequestConfig.setId(null);
		ScheduledRequestConfig savedScheduledRequestConfig = getTestScheduledRequestConfig();

		restControllerTester.performCreateTest(mockMvc, repository, CREATE_REQUEST_BODY_FILE_NAME,
				CREATE_RESPONSE_BODY_FILE_NAME, unsavedScheduledRequestConfig, savedScheduledRequestConfig);
	}

	@Test
	public void retrieveTest() throws Exception {
		ScheduledRequestConfig testScheduledRequestConfig = getTestScheduledRequestConfig();

		restControllerTester.performRetrieveTest(mockMvc, repository, CREATE_RESPONSE_BODY_FILE_NAME,
				testScheduledRequestConfig);
	}

	@Test
	public void updateTest() throws Exception {
		ScheduledRequestConfig originalEntityInstance = getTestScheduledRequestConfig();
		ScheduledRequestConfig expectedEntityInstance = getAlternativeTestScheduledRequestConfig();
		expectedEntityInstance.setId(originalEntityInstance.getId());

		restControllerTester.performUpdateTest(mockMvc, repository, UPDATE_REQUEST_BODY_FILE_NAME,
				UPDATE_RESPONSE_BODY_FILE_NAME, originalEntityInstance, expectedEntityInstance);
	}

	@Test
	public void deleteTest() throws Exception {
		ScheduledRequestConfig entityInstance = getTestScheduledRequestConfig();

		restControllerTester.performDeleteRequest(mockMvc, repository, entityInstance);
	}


	/* ********************************************************************************************************** */
	/* ********************************************* PRIVATE METHODS ******************************************** */
	/* ********************************************************************************************************** */


	private static ScheduledRequestConfig getTestScheduledRequestConfig() {
		ScheduledRequestConfig scheduledRequestConfig = new ScheduledRequestConfig();
		scheduledRequestConfig.setId(1);
		scheduledRequestConfig.setTriggerConfig(TriggerConfigControllerTest.getTestTriggerConfig());
		scheduledRequestConfig.setRequestType(ScheduledRequestConfigTest.TEST_REQUEST_TYPE);
		scheduledRequestConfig.setRequestId(ScheduledRequestConfigTest.TEST_REQUEST_ID);

		return scheduledRequestConfig;
	}

	private static ScheduledRequestConfig getAlternativeTestScheduledRequestConfig() {
		ScheduledRequestConfig scheduledRequestConfig = new ScheduledRequestConfig();
		scheduledRequestConfig.setId(2);
		scheduledRequestConfig.setTriggerConfig(TriggerConfigControllerTest.getAlternativeTestTriggerConfig());
		scheduledRequestConfig.setRequestType(ScheduledRequestConfigTest.OTHER_TEST_REQUEST_TYPE);
		scheduledRequestConfig.setRequestId(ScheduledRequestConfigTest.OTHER_TEST_REQUEST_ID);

		return scheduledRequestConfig;
	}

}
