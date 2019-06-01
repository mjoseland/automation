package name.joseland.mal.automation.core.rest.out;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;

public class TimedHttpResponseTest {

    private static LocalDateTime TIME_REQUESTED;
    private static LocalDateTime TIME_RECEIVED;
    private static HttpResponse<String> MOCK_RESPONSE;

    private static TimedHttpResponse<String> MOCK_TIMED_RESPONSE;


    @BeforeClass
    public static void beforeClass() {
        MOCK_RESPONSE = Mockito.mock(HttpResponse.class);
        TIME_REQUESTED = LocalDateTime.now();
        TIME_RECEIVED = LocalDateTime.now().plusNanos(500);

        MOCK_TIMED_RESPONSE = getMockTimedResponse();
    }


    /* ********************************************************************************************************** */
    /* ************************************************** TESTS ************************************************* */
    /* ********************************************************************************************************** */


    @Test(expected = NullPointerException.class)
    public void buildWithNullResponseTest() {
        TimedHttpResponse.build(null, TIME_REQUESTED, TIME_RECEIVED);
    }

    @Test(expected = NullPointerException.class)
    public void buildWithNullTimeRequestedTest() {
        TimedHttpResponse.build(MOCK_RESPONSE, null, TIME_RECEIVED);
    }

    @Test(expected = NullPointerException.class)
    public void buildWithNullTimeReceivedTest() {
        TimedHttpResponse.build(MOCK_RESPONSE, TIME_REQUESTED, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildWithTimeRequestedAfterTimeReceived() {
        TimedHttpResponse.build(MOCK_RESPONSE, TIME_RECEIVED, TIME_REQUESTED);
    }

    @Test
    public void toStringTest() {
        String expectedResult = "httpResponse=[Mock for HttpResponse, hashCode: " +
                MOCK_RESPONSE.hashCode() +
                "], timeRequested=" +
                TIME_REQUESTED.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) +
                ", timeReceived=" +
                TIME_RECEIVED.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        assertEquals(expectedResult, MOCK_TIMED_RESPONSE.toString());
    }

    @Test
    public void equalsTest() {
        TimedHttpResponse expectedMockTimedResponse =
                TimedHttpResponse.build(MOCK_RESPONSE, TIME_REQUESTED, TIME_RECEIVED);

        assertEquals(expectedMockTimedResponse, MOCK_TIMED_RESPONSE);

        assertNotEquals(MOCK_TIMED_RESPONSE, null);
        assertNotEquals(MOCK_TIMED_RESPONSE, 0);
    }

    @Test
    public void hashCodeTest() {
        TimedHttpResponse expectedMockTimedResponse = getMockTimedResponse();

        // assert that two equivalent objects have the same hash code
        assertEquals(expectedMockTimedResponse.hashCode(), MOCK_TIMED_RESPONSE.hashCode());
    }

    @Test
    public void compareToTest() {
        // not equal to null
        assertNotSame(0, MOCK_TIMED_RESPONSE.compareTo(null));
        // if (this == 0)
        assertEquals(0, MOCK_TIMED_RESPONSE.compareTo(MOCK_TIMED_RESPONSE));

        TimedHttpResponse<String> expectedMockTimedResponse = getMockTimedResponse();
        assertEquals(0, expectedMockTimedResponse.compareTo(MOCK_TIMED_RESPONSE));

        // all fields the same as the mock response except for the request
        TimedHttpResponse<String> httpResponseDiffResponse = TimedHttpResponse.build(
                Mockito.mock(HttpResponse.class),
                TIME_REQUESTED,
                TIME_RECEIVED);
        assertNotSame(0, MOCK_TIMED_RESPONSE.compareTo(httpResponseDiffResponse));

        // all fields the same as the mock response except for time requested
        TimedHttpResponse<String> timeRequestedDiffResponse = TimedHttpResponse.build(
                MOCK_RESPONSE,
                TIME_REQUESTED.plusNanos(50),
                TIME_RECEIVED);
        assertNotSame(0, MOCK_TIMED_RESPONSE.compareTo(timeRequestedDiffResponse));

        // all fields the same as the mock response except for time received
        TimedHttpResponse<String> timeReceivedDiffResponse = TimedHttpResponse.build(
                MOCK_RESPONSE,
                TIME_REQUESTED,
                TIME_RECEIVED.plusNanos(50));
        assertNotSame(0, MOCK_TIMED_RESPONSE.compareTo(timeReceivedDiffResponse));
    }

    @Test
    public void gettersTest() {
        assertEquals(TIME_REQUESTED, MOCK_TIMED_RESPONSE.getTimeRequested());
        assertEquals(TIME_RECEIVED, MOCK_TIMED_RESPONSE.getTimeReceived());

        assertEquals(MOCK_RESPONSE.statusCode(), MOCK_TIMED_RESPONSE.statusCode());
        assertEquals(MOCK_RESPONSE.request(), MOCK_TIMED_RESPONSE.request());
        assertEquals(MOCK_RESPONSE.previousResponse(), MOCK_TIMED_RESPONSE.previousResponse());
        assertEquals(MOCK_RESPONSE.headers(), MOCK_TIMED_RESPONSE.headers());
        assertEquals(MOCK_RESPONSE.body(), MOCK_TIMED_RESPONSE.body());
        assertEquals(MOCK_RESPONSE.sslSession(), MOCK_TIMED_RESPONSE.sslSession());
        assertEquals(MOCK_RESPONSE.uri(), MOCK_TIMED_RESPONSE.uri());
        assertEquals(MOCK_RESPONSE.version(), MOCK_TIMED_RESPONSE.version());
    }


    /* ********************************************************************************************************** */
    /* ********************************************* PRIVATE METHODS ******************************************** */
    /* ********************************************************************************************************** */


    private static TimedHttpResponse<String> getMockTimedResponse() {
        return TimedHttpResponse.build(MOCK_RESPONSE, TIME_REQUESTED, TIME_RECEIVED);
    }

}
