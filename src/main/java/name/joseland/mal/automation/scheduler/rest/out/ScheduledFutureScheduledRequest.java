package name.joseland.mal.automation.scheduler.rest.out;

import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.ScheduledFuture;

public class ScheduledFutureScheduledRequest implements ScheduledRequest {
    @Value("${automation.scheduler.scheduled-request.interrupt-if-running}")
    private static boolean interruptIfRunning;

    private final ScheduledFuture scheduledFuture;

    private boolean active;


    ScheduledFutureScheduledRequest(ScheduledFuture scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
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
