package name.joseland.mal.automation.core.rest.out;

import org.junit.Test;
import org.mockito.Mockito;

import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TimedResponseHistoryTest {


	/* ********************************************************************************************************** */
	/* ************************************************** TESTS ************************************************* */
	/* ********************************************************************************************************** */


	@Test
	public void buildTest() {
		TimedResponseHistory<String> testResponseHistory = TimedResponseHistory.build();
		assertNotNull(testResponseHistory);

		Duration testDuration = Duration.ofMillis(500);
		TimedResponseHistory<String> testResponseHistoryWithDuration = TimedResponseHistory.build(testDuration);
		assertNotNull(testResponseHistoryWithDuration);
		assertEquals(testDuration, testResponseHistoryWithDuration.getMinDuration());
	}

	@Test
	public void setGetMinDurationTest() {
		Duration testDuration = Duration.ofMillis(500);

		TimedResponseHistory<String> testResponseHistory = TimedResponseHistory.build();
		assertNull(testResponseHistory.getMinDuration());
		testResponseHistory.setMinDuration(testDuration);
		assertEquals(testDuration, testResponseHistory.getMinDuration());
	}

	@Test
	public void addMultipleTimedResponseTest() {
		// assume that the test will take less than 10 seconds
		Duration testDuration = Duration.ofMillis((long) 10 * 1000);

		TimedHttpResponse<String> firstTimedResponse = getMockTimedResponse(
				LocalDateTime.now().plusNanos((long) -102 * 1000), LocalDateTime.now().plusNanos(-10));

		TimedHttpResponse<String> secondTimedResponse = getMockTimedResponse(
				LocalDateTime.now().plusNanos((long) -101 * 1000), LocalDateTime.now().plusNanos(-1));

		TimedResponseHistory<String> timedResponseHistory = TimedResponseHistory.build(testDuration);
		timedResponseHistory.add(firstTimedResponse);
		timedResponseHistory.add(secondTimedResponse);

		// confirm that both of the timed HTTP responses are in the history
		assertEquals(2, timedResponseHistory.getHistory().size());
		assertTrue(timedResponseHistory.getHistory().contains(firstTimedResponse));
		assertTrue(timedResponseHistory.getHistory().contains(secondTimedResponse));
	}

	@Test
	public void timedResponseExpiryTest() throws InterruptedException {
		// the duration (in ms)
		long delayTime = 1000;

		Duration testDuration = Duration.ofMillis(delayTime);
		TimedResponseHistory<String> timedResponseHistory = TimedResponseHistory.build(testDuration);

		TimedHttpResponse<String> timedResponse = getMockTimedResponse(
				LocalDateTime.now().plusNanos((long) -100 * 1000 + 10), LocalDateTime.now().plusNanos(-10));
		timedResponseHistory.add(timedResponse);

		// assert that timedResponse is in the history
		assertEquals(1, timedResponseHistory.getHistory().size());
		assertTrue(timedResponseHistory.getHistory().contains(timedResponse));

		// wait for the delay time
		Thread.sleep(delayTime);

		assertEquals(0, timedResponseHistory.getHistory().size());
	}


	/* ********************************************************************************************************** */
	/* ********************************************* PRIVATE METHODS ******************************************** */
	/* ********************************************************************************************************** */


	private static TimedHttpResponse<String> getMockTimedResponse(LocalDateTime timeRequested,
			LocalDateTime timeReceived) {
		@SuppressWarnings("unchecked")
		HttpResponse<String> mockResponse = (HttpResponse<String>) Mockito.mock(HttpResponse.class);

		return TimedHttpResponse.build(mockResponse, timeRequested, timeReceived);
	}

}
