package name.joseland.mal.automation.scheduler.rest.out;

import name.joseland.mal.automation.core.rest.out.TimedResponseHistory;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.ScheduledFuture;

public class ScheduledFutureScheduledRequest implements ScheduledRequest {
    @Value("${automation.scheduler.scheduled-request.interrupt-if-running}")
    private static boolean INTERRUPT_IF_RUNNING;

    private final ScheduledFuture scheduledFuture;
    private final TimedResponseHistory<String> responseHistory;

    private boolean active;


    ScheduledFutureScheduledRequest(ScheduledFuture scheduledFuture, TimedResponseHistory<String> responseHistory) {
        this.scheduledFuture = scheduledFuture;
        this.responseHistory = responseHistory;
        this.active = true;
    }


    /* ********************************************************************************************************** */
    /* ********************************************* PUBLIC METHODS ********************************************* */
    /* ********************************************************************************************************** */


    /**
     * Cancels the scheduled request. Once cancelled, objects can't be re-activated and will not initiate any new
     * requests.
     *
     * {@link #isActive()} will return false.
     */
    @Override
    public void cancel() {
        scheduledFuture.cancel(false);
        this.active = false;
    }


    /* ********************************************************************************************************** */
    /* ********************************************* GETTERS/SETTERS ******************************************** */
    /* ********************************************************************************************************** */


    /**
     * Gets a defensive copy of the history of responses received by requests sent by this object. Implementing types
     * may control the history's cutoff time.
     *
     * @return responses received by requests sent by this object
     */
    @Override
    public TimedResponseHistory<String> getResponseHistory() {
        return responseHistory;
    }

    /**
     * Returns true if this object is active. The result of this method can only change once and can only
     * change from true to false.
     *
     * @return  true if this object is active
     */
    @Override
    public boolean isActive() {
        return active;
    }

}
