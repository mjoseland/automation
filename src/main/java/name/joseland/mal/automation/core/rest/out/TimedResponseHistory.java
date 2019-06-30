package name.joseland.mal.automation.core.rest.out;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Supplier;

/**
 * Stores a history of {@link TimedHttpResponse} objects for a mutable duration after they were received. The
 * completeness of the history isn't guaranteed, ie. when the minDuration field has been increased to one that includes
 * responses that have previously been excluded.
 *
 * @param <T> the body type of responses
 */
public class TimedResponseHistory<T> {

    private final Supplier<SortedSet<TimedHttpResponse<T>>> sortedSetSupplier = TreeSet::new;

    private final SortedSet<TimedHttpResponse<T>> history;

    // the minimum length of time to retain responses in the history
    // ie. responses that were received after (minDuration before LocalDateTime.now()) are guaranteed to be retained
    // if null, all responses will be retained
    private Duration minDuration;


    private TimedResponseHistory() {
        this.history = sortedSetSupplier.get();
        this.minDuration = null;
    }

    /**
     * Static builder.
     *
     * @param <U>   the response body type of all history items
     * @return      the new TimedResponseHistory
     */
    public static <U> TimedResponseHistory<U> build() {
        return new TimedResponseHistory<>();
    }

    /**
     * Static builder with minDuration.
     *
     * @param minDuration   the minimum duration to store history items after they were received
     * @param <U>           the response body type of all history items
     * @return              the new TimedResponseHistory
     * @throws IllegalArgumentException if minDuration is negative or zero
     */
    public static <U> TimedResponseHistory<U> build(Duration minDuration) {
        TimedResponseHistory<U> timedResponseHistory = new TimedResponseHistory<>();
        timedResponseHistory.setMinDuration(minDuration);

        return timedResponseHistory;
    }


    /* ********************************************************************************************************** */
    /* ********************************************* GETTERS/SETTERS ******************************************** */
    /* ********************************************************************************************************** */


    /**
     * Gets a defensive copy of {@link TimedHttpResponse} objects from requests sent, sorted by time received (asc).
     *
     * @return  responses to requests sent by this object
     */
    public SortedSet<TimedHttpResponse<T>> getHistory() {
        removeExpiredResponses();

        SortedSet<TimedHttpResponse<T>> defensiveCopy = sortedSetSupplier.get();
        defensiveCopy.addAll(history);

        return defensiveCopy;
    }

    /**
     * Adds a response to the history.
     *
     * @param responses the responses to add to the history
     */
    @SafeVarargs
    public final void add(TimedHttpResponse<T>... responses) {
        history.addAll(Arrays.asList(responses));

        removeExpiredResponses();
    }

    /**
     * Get the minimum duration that responses will be retained after being received.
     *
     * @return the minimum duration
     */
    public Duration getMinDuration() {
        return minDuration;
    }

    /**
     * @param minDuration the new minimum duration
	 * @throws IllegalArgumentException if minDuration is negative or zero
     */
    public void setMinDuration(Duration minDuration) {
        if (minDuration.isNegative() || minDuration.isZero())
            throw new IllegalArgumentException("Min duration negative or zero");

        this.minDuration = minDuration;

		removeExpiredResponses();
    }


    /* ********************************************************************************************************** */
    /* ********************************************* PRIVATE METHODS ******************************************** */
    /* ********************************************************************************************************** */


    /**
     * Remove the items in the history that weren't received after minDuration time before now.
     */
	private void removeExpiredResponses() {
        history.removeIf(response -> !responseIsInsideDuration(response));
    }

    /**
     * @return true if the response was received after minDuration time before now
     */
    private boolean responseIsInsideDuration(TimedHttpResponse<T> response) {
        return response.getTimeReceived()
                .isAfter(LocalDateTime.now().minus(getMinDuration()));
    }

}
