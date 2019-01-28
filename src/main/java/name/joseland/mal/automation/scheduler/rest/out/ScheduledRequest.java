package name.joseland.mal.automation.scheduler.rest.out;

import name.joseland.mal.automation.core.rest.out.TimedResponseHistory;

/**
 * Attempts to send identical HTTP requests on a set schedule. Neither the request nor the schedule it runs on are
 * mutable, with the exception of the ability to cancel future requests (eg. with {@link #cancel()}.
 *
 * Objects are immediately active on creation and can be cancelled only once. Once cancelled, objects can't be
 * re-activated and will not initiate any new requests.
 */
public interface ScheduledRequest {

    /**
     * Cancels the scheduled request. Once cancelled, objects can't be re-activated and will not initiate any new
     * requests.
     *
     * {@link #isActive()} will return false.
     */
    void cancel();


    /* ********************************************************************************************************** */
    /* ********************************************* GETTERS/SETTERS ******************************************** */
    /* ********************************************************************************************************** */


    /**
     * Gets a defensive copy of the history of responses received by requests sent by this object. Implementing types
     * may control the history's cutoff time.
     *
     * @return responses received by requests sent by this object
     */
    TimedResponseHistory<String> getResponseHistory();

    /**
     * Returns true if this object is active. The result of this method can only change once and can only
     * change from true to false.
     *
     * @return  true if this object is active
     */
    boolean isActive();

}
