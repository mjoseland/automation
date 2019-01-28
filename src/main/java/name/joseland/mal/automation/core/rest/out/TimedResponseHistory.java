package name.joseland.mal.automation.core.rest.out;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Supplier;

/**
 * Stores a history of {@link TimedHttpResponse} objects for a mutable duration after they were received. The
 * completeness of the history isn't guaranteed, ie. when the minDuration field has been increased to one that includes
 * responses that have previously been excluded.
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

    public static TimedResponseHistory build() {
        return new TimedResponseHistory();
    }

    public static TimedResponseHistory build(Duration minDuration) {
        TimedResponseHistory timedResponseHistory = new TimedResponseHistory();
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
        SortedSet<TimedHttpResponse<T>> defensiveCopy = sortedSetSupplier.get();
        defensiveCopy.addAll(history);

        return defensiveCopy;
    }

    /**
     * Adds a response to the history if it is within the required duration.
     *
     * @param response the response to attempt to add to the history.
     */
    public void addTimedResponse(TimedHttpResponse<T> response) {
        if (responseIsInsideDuration(response))
            history.add(response);
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
     */
    public void setMinDuration(Duration minDuration) {
        this.minDuration = minDuration;

        // remove history that isn't inside the new minDuration
        history.removeIf(response -> !responseIsInsideDuration(response));
    }


    /* ********************************************************************************************************** */
    /* ********************************************* PRIVATE METHODS ******************************************** */
    /* ********************************************************************************************************** */


    private boolean responseIsInsideDuration(TimedHttpResponse<T> response) {
        return response.getTimeReceived()
                .isAfter(LocalDateTime.now().minus(getMinDuration()));
    }

}
