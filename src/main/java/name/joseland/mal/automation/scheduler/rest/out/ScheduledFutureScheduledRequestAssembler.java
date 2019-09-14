package name.joseland.mal.automation.scheduler.rest.out;

import name.joseland.mal.automation.core.rest.HttpRequestException;
import name.joseland.mal.automation.core.rest.out.TimedRequestSender;
import name.joseland.mal.automation.core.rest.out.internal.RequestRepositoryRetriever;
import name.joseland.mal.automation.core.rest.out.internal.exception.InternalServiceNotFoundException;
import name.joseland.mal.automation.core.rest.out.internal.exception.InvalidInternalServiceRequestParameter;
import name.joseland.mal.automation.scheduler.db.ScheduledRequestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

/**
 * Assembles {@link ScheduledRequest} instances when supplied with a {@link ScheduledRequestConfig}.
 */
@Component
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
    public ScheduledFutureScheduledRequest assemble(@NonNull ScheduledRequestConfig config)
            throws InterruptedException {
        Objects.requireNonNull(config);

        // HttpRequest retrieved from the request repository at the url stored in the config
        HttpRequest request;
        try {
            request = requestRepositoryRetriever.retrieveStoredRequest(config.getRequestType(), config.getRequestId());
        } catch (IOException | InternalServiceNotFoundException | URISyntaxException |
                InvalidInternalServiceRequestParameter e) {
            throw new HttpRequestException(e.getMessage());
        }

        // Callable that sends the request and returns its body
        TimedRequestSender<String> timedRequestSender = TimedRequestSender.build(request,
                HttpResponse.BodyHandlers.ofString());

        // Trigger built from config's TriggerConfig
        Trigger trigger = triggerAssembler.assemble(config.getTriggerConfig());

        // Runnable that will send the timed request
        Runnable requestSendRunnable = getRequestSendRunnable(timedRequestSender);

        // ScheduledFuture that runs requestSendRunnable when triggered by trigger
        ScheduledFuture scheduledFuture = taskScheduler.schedule(requestSendRunnable, trigger);

        // return a new ScheduledFutureScheduledRequest
        return new ScheduledFutureScheduledRequest(scheduledFuture);
    }


    /* ********************************************************************************************************** */
    /* ********************************************* PRIVATE METHODS ******************************************** */
    /* ********************************************************************************************************** */


    private Runnable getRequestSendRunnable(TimedRequestSender<String> timedRequestSender) {
        return () -> {
            try {
                timedRequestSender.call();
            } catch (IOException | InterruptedException e) {
                throw new HttpRequestException(e.getMessage());
            }
        };
    }

}
