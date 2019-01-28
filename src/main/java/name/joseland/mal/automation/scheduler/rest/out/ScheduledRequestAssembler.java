package name.joseland.mal.automation.scheduler.rest.out;

import name.joseland.mal.automation.scheduler.db.ScheduledRequestConfig;
import org.springframework.lang.NonNull;

/**
 * Assembles {@link ScheduledRequest} instances when supplied with a {@link ScheduledRequestConfig}.
 */
public interface ScheduledRequestAssembler {

    /**
     * Assembles a {@link ScheduledRequest} from a {@link ScheduledRequestConfig}.
     *
     * @param config    the ScheduledRequestConfig to assemble the ScheduledRequest from
     * @return          the ScheduledRequest
     */
    ScheduledRequest assemble(@NonNull ScheduledRequestConfig config);

}
