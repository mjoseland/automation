package name.joseland.mal.automation.scheduler.rest.in;

import name.joseland.mal.automation.scheduler.db.TriggerConfig;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class TriggerConfigAssembler implements ResourceAssembler<TriggerConfig, Resource<TriggerConfig>> {

    @Override
    public Resource<TriggerConfig> toResource(TriggerConfig triggerConfig) {
        return new Resource<>(triggerConfig,
                linkTo(methodOn(TriggerConfigController.class).retrieve(triggerConfig.getId())).withSelfRel(),
                linkTo(methodOn(TriggerConfigController.class).retrieveAll()).withRel("trigger-configs"));
    }

}
