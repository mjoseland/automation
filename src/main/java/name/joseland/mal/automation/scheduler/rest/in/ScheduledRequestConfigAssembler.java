package name.joseland.mal.automation.scheduler.rest.in;

import name.joseland.mal.automation.scheduler.db.ScheduledRequestConfig;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class ScheduledRequestConfigAssembler
        implements ResourceAssembler<ScheduledRequestConfig, Resource<ScheduledRequestConfig>> {

    @Override
    public Resource<ScheduledRequestConfig> toResource(ScheduledRequestConfig scheduledRequestConfig) {
        return new Resource<>(scheduledRequestConfig,
                linkTo(methodOn(ScheduledRequestConfigController.class)
                        .retrieve(scheduledRequestConfig.getId())).withSelfRel(),
                linkTo(methodOn(ScheduledRequestConfigController.class)
                        .retrieveAll()).withRel("scheduled-request-configs"));
    }

}
