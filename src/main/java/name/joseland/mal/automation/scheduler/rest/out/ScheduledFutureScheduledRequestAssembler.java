package name.joseland.mal.automation.scheduler.rest.out;

import name.joseland.mal.automation.core.rest.HttpRequestException;
import name.joseland.mal.automation.core.rest.out.TimedHttpResponse;
import name.joseland.mal.automation.core.rest.out.TimedRequestSender;
import name.joseland.mal.automation.core.rest.out.TimedResponseHistory;
import name.joseland.mal.automation.core.rest.out.internal.RequestRepositoryRetriever;
import name.joseland.mal.automation.scheduler.db.ScheduledRequestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ScheduledFuture;

/**
 * Assembles {@link ScheduledRequest} instances when supplied with a {@link ScheduledRequestConfig}.
 */
@Component
@Scope(value = "singleton")
public class ScheduledFutureScheduledRequestAssembler implements ScheduledRequestAssembler {

    private final ThreadPoolTaskScheduler taskScheduler;
    private final RequestRepositoryRetriever requestRepositoryRetriever;
    private final TriggerAssembler triggerAssembler;


    ScheduledFutureScheduledRequestAssembler(@Autowired ThreadPoolTaskScheduler taskScheduler,
                                             @Autowired RequestRepositoryRetriever requestRepositoryRetriever,
                                             @Autowired TriggerAssembler triggerAssembler) {
        this.taskScheduler = taskScheduler;
        this.requestRepositoryRetriever = requestRepositoryRetriever;
        this.triggerAssembler = triggerAssembler;
    }

    /**
     * Assembles a {@link ScheduledFutureScheduledRequest} from a {@link ScheduledRequestConfig}.
     *
     * @param config    the ScheduledRequestConfig to assemble the ScheduledRequest from
     * @return          the ScheduledRequest
     */
    @Override
    public ScheduledFutureScheduledRequest assemble(ScheduledRequestConfig config) {
        // HttpRequest retrieved from the request repository at the url stored in the config
        HttpRequest request;
        try {
            request = requestRepositoryRetriever.fromResourcePath(config.getRequestRepositoryMapping());
        } catch (IOException | InterruptedException e) {
            throw new HttpRequestException(e.getMessage());
        }

        // Callable that sends the request and returns its body
        TimedRequestSender<String> timedRequestSender = TimedRequestSender.build(request,
                HttpResponse.BodyHandlers.ofString());

        // Trigger built from config's TriggerConfig
        Trigger trigger = triggerAssembler.assemble(config.getTriggerConfig());

        // new, empty TimedResponseHistory<String>
        TimedResponseHistory<String> responseHistory = TimedResponseHistory.build();

        // Runnable that will send the timed request and record its response in response history
        Runnable requestSendRunnable = getRequestSendRunnable(timedRequestSender, responseHistory);

        // ScheduledFuture that runs requestSendRunnable when triggered by trigger
        ScheduledFuture scheduledFuture = taskScheduler.schedule(requestSendRunnable, trigger);

        // return a new ScheduledFutureScheduledRequest
        return new ScheduledFutureScheduledRequest(scheduledFuture, responseHistory);
    }


    /* ********************************************************************************************************** */
    /* ********************************************* PRIVATE METHODS ******************************************** */
    /* ********************************************************************************************************** */


    private Runnable getRequestSendRunnable(TimedRequestSender<String> timedRequestSender,
                                            TimedResponseHistory<String> responseHistory) {
        return () -> {
            // get the response
            TimedHttpResponse<String> response;
            try {
                response = timedRequestSender.call();
            } catch (IOException | InterruptedException e) {
                throw new HttpRequestException(e.getMessage());
            }

            // add the response to the response history
            responseHistory.addTimedResponse(response);
        };
    }

}
