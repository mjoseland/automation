package name.joseland.mal.automation.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Class for testing data API {@link org.springframework.web.bind.annotation.RestController} beans.
 *
 * @param <T>  the entity type
 * @param <ID> the entity's ID type
 */
public class GenericRestControllerTester<T, ID> {

	private final String contextPath;
	private final Function<T, ID> getIdFunc;


	public GenericRestControllerTester(String contextPath, Function<T, ID> getIdFunc) {
		this.contextPath = Objects.requireNonNull(contextPath);
		this.getIdFunc = Objects.requireNonNull(getIdFunc);
	}


	/* ********************************************************************************************************** */
	/* ******************************************** PROTECTED METHODS ******************************************* */
	/* ********************************************************************************************************** */


	public final void performRetrieveAllTest(MockMvc mockMvc, JpaRepository<T, ID> repository,
			String expectedResponseFileName, List<T> entityInstances) throws Exception {
		// mock repository.findAll()
		given(repository.findAll()).willReturn(entityInstances);

		String expectedResponseJsonStr = retrieveJsonFileAsString(expectedResponseFileName);

		// perform get with mockMvc and verify the response
		mockMvc.perform(get(contextPath)
				.accept(MediaTypes.HAL_JSON_UTF8))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8_VALUE))
				.andExpect(content().json(expectedResponseJsonStr, true));
	}

	public void performCreateTest(MockMvc mockMvc, JpaRepository<T, ID> repository, String requestFileName,
			String expectedResponseFileName, T unsavedEntityInstance, T savedEntityInstance) throws Exception {
		// mock repository.save(..)
		given(repository.save(refEq(unsavedEntityInstance))).willReturn(savedEntityInstance);

		// JSON body that is translated to unsavedEntityInstance by @RequestBody
		String requestBodyJsonStr = retrieveJsonFileAsString(requestFileName);
		// expected response to POST
		String expectedResponseBodyJsonStr = retrieveJsonFileAsString(expectedResponseFileName);

		// perform post with mockMvc and verify the response
		mockMvc.perform(post(getContextPath())
				.contentType(MediaTypes.HAL_JSON_UTF8)
				.content(requestBodyJsonStr)
				.accept(MediaTypes.HAL_JSON_UTF8))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8_VALUE))
				.andExpect(content().json(expectedResponseBodyJsonStr, true));
	}

	public void performRetrieveTest(MockMvc mockMvc, JpaRepository<T, ID> repository, String expectedResponseFileName,
			T entityInstance) throws Exception {
		ID entityInstanceId = getIdFunc.apply(entityInstance);

		// mock repository.findById(..)
		given(repository.findById(entityInstanceId)).willReturn(Optional.of(entityInstance));

		String urlTemplate = getContextPath() + "/" + entityInstanceId;

		// expected response
		String expectedResponseBodyJsonStr = retrieveJsonFileAsString(expectedResponseFileName);

		mockMvc.perform(get(urlTemplate)
				.accept(MediaTypes.HAL_JSON_UTF8))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8_VALUE))
				.andExpect(content().json(expectedResponseBodyJsonStr, true));
	}

	public void performUpdateTest(MockMvc mockMvc, JpaRepository<T, ID> repository, String requestFileName,
			String expectedResponseFileName, T originalEntityInstance, T expectedEntityInstance) throws Exception {
		ID originalEntityInstanceId = getIdFunc.apply(originalEntityInstance);

		// mock repository.findById(..) for the primary test internal request's id
		given(repository.findById(originalEntityInstanceId)).willReturn(Optional.of(originalEntityInstance));

		// mock repository.save(..)
		given(repository.save(expectedEntityInstance)).willReturn(expectedEntityInstance);

		String urlTemplate = getContextPath() + "/" + originalEntityInstanceId;

		// JSON body that is translated to originalEntityInstance by @RequestBody
		String requestBodyJsonStr = retrieveJsonFileAsString(requestFileName);
		// expected response to POST
		String expectedResponseBodyJsonStr = retrieveJsonFileAsString(expectedResponseFileName);

		// perform get with mock MVC and verify the response
		mockMvc.perform(put(urlTemplate)
				.contentType(MediaTypes.HAL_JSON_UTF8)
				.content(requestBodyJsonStr)
				.accept(MediaTypes.HAL_JSON_UTF8))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8_VALUE))
				.andExpect(content().json(expectedResponseBodyJsonStr, true));
	}

	public void performDeleteRequest(MockMvc mockMvc, JpaRepository<T, ID> repository, T entityInstance)
			throws Exception {
		ID entityInstanceId = getIdFunc.apply(entityInstance);

		// mock repository.findById(..)
		given(repository.findById(entityInstanceId)).willReturn(Optional.of(entityInstance));

		String urlTemplate = getContextPath() + "/" + entityInstanceId;

		mockMvc.perform(delete(urlTemplate)
				.accept(MediaTypes.HAL_JSON_UTF8))
				.andDo(print())
				.andExpect(status().isNoContent())
				.andExpect(content().string(""));
	}


	/* ********************************************************************************************************** */
	/* ********************************************* PRIVATE METHODS ******************************************** */
	/* ********************************************************************************************************** */


	private String getContextPath() {
		return contextPath;
	}

	private String retrieveJsonFileAsString(String fileName) throws FileNotFoundException {
		File file = new File(fileName);
		Scanner sc = new Scanner(file).useDelimiter("\\Z");
		String internalRequestAsStr = sc.next();
		sc.close();
		return internalRequestAsStr;
	}

}
