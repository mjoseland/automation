package name.joseland.mal.automation.scheduler.rest.out;

import name.joseland.mal.automation.scheduler.db.TriggerConfig;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

@Component
public class TriggerAssembler {

    /**
     * Assembles a {@link Trigger) from a {@link TriggerConfig}.
     *
     * @param config                        the config to assemble the Trigger from
     * @return                              the Trigger
     * @throws IllegalArgumentException     if a Trigger cannot be assembled from the config
     */
    public Trigger assemble(@NonNull TriggerConfig config) {
        switch(config.getType()) {
            case CRON:
                return new CronTrigger(config.getCronExpression());
            default:
                throw new IllegalArgumentException("Cannot assemble from trigger with Type: " + config.getType());
        }
    }

}
