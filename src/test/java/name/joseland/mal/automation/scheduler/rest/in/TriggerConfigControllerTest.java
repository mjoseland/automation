package name.joseland.mal.automation.scheduler.rest.in;

import name.joseland.mal.automation.core.GenericRestControllerTester;
import name.joseland.mal.automation.scheduler.db.TriggerConfig;
import name.joseland.mal.automation.scheduler.db.TriggerConfigRepository;
import name.joseland.mal.automation.scheduler.db.TriggerConfigTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@WebMvcTest(TriggerConfigController.class)
@Import(TriggerConfigAssembler.class)
@RunWith(SpringRunner.class)
public class TriggerConfigControllerTest {

	private static final String RETRIEVE_ALL_RESPONSE_BODY_FILE_NAME =
			"src/test/resources/json/scheduler/trigger_config_retrieve_all_response.json";

	private static final String CREATE_REQUEST_BODY_FILE_NAME =
			"src/test/resources/json/scheduler/trigger_config_create_request.json";
	// also used for retrieveTest
	private static final String CREATE_RESPONSE_BODY_FILE_NAME =
			"src/test/resources/json/scheduler/trigger_config_create_response.json";

	private static final String UPDATE_REQUEST_BODY_FILE_NAME =
			"src/test/resources/json/scheduler/trigger_config_update_request.json";
	private static final String UPDATE_RESPONSE_BODY_FILE_NAME =
			"src/test/resources/json/scheduler/trigger_config_update_response.json";

	private final GenericRestControllerTester<TriggerConfig, Integer> restControllerTester;

	@MockBean
	private TriggerConfigRepository repository;

	@Autowired
	private MockMvc mockMvc;

	public TriggerConfigControllerTest() {
		this.restControllerTester = new GenericRestControllerTester<>("/trigger-configs",
				TriggerConfig::getId);
	}


	/* ********************************************************************************************************** */
	/* ************************************************** TESTS ************************************************* */
	/* ********************************************************************************************************** */


	@Test
	public void retrieveAllTest() throws Exception {
		List<TriggerConfig> requestsToAddToRepository = new ArrayList<>();
		requestsToAddToRepository.add(getTestTriggerConfig());
		requestsToAddToRepository.add(getAlternativeTestTriggerConfig());

		restControllerTester.performRetrieveAllTest(mockMvc, repository, RETRIEVE_ALL_RESPONSE_BODY_FILE_NAME,
				requestsToAddToRepository);
	}

	@Test
	public void createTest() throws Exception {
		TriggerConfig unsavedTriggerConfig = getTestTriggerConfig();
		unsavedTriggerConfig.setId(null);
		TriggerConfig savedTriggerConfig = getTestTriggerConfig();

		restControllerTester.performCreateTest(mockMvc, repository, CREATE_REQUEST_BODY_FILE_NAME,
				CREATE_RESPONSE_BODY_FILE_NAME, unsavedTriggerConfig, savedTriggerConfig);
	}

	@Test
	public void retrieveTest() throws Exception {
		TriggerConfig testTriggerConfig = getTestTriggerConfig();

		restControllerTester.performRetrieveTest(mockMvc, repository, CREATE_RESPONSE_BODY_FILE_NAME,
				testTriggerConfig);
	}

	@Test
	public void updateTest() throws Exception {
		TriggerConfig originalEntityInstance = getTestTriggerConfig();
		TriggerConfig expectedEntityInstance = getAlternativeTestTriggerConfig();
		expectedEntityInstance.setId(originalEntityInstance.getId());

		restControllerTester.performUpdateTest(mockMvc, repository, UPDATE_REQUEST_BODY_FILE_NAME,
				UPDATE_RESPONSE_BODY_FILE_NAME, originalEntityInstance, expectedEntityInstance);
	}

	@Test
	public void deleteTest() throws Exception {
		TriggerConfig entityInstance = getTestTriggerConfig();

		restControllerTester.performDeleteRequest(mockMvc, repository, entityInstance);
	}


	/* ********************************************************************************************************** */
	/* ********************************************* PRIVATE METHODS ******************************************** */
	/* ********************************************************************************************************** */


	static TriggerConfig getTestTriggerConfig() {
		TriggerConfig triggerConfig = new TriggerConfig();
		triggerConfig.setId(1);
		triggerConfig.setType(TriggerConfigTest.TEST_TYPE);
		triggerConfig.setCronExpression(TriggerConfigTest.TEST_CRON_EXPRESSION);

		return triggerConfig;
	}

	static TriggerConfig getAlternativeTestTriggerConfig() {
		TriggerConfig triggerConfig = new TriggerConfig();
		triggerConfig.setId(2);
		triggerConfig.setType(TriggerConfigTest.OTHER_TEST_TYPE);
		triggerConfig.setCronExpression(TriggerConfigTest.OTHER_TEST_CRON_EXPRESSION);

		return triggerConfig;
	}

}
