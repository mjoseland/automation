package name.joseland.mal.automation.scheduler.rest.in;

import name.joseland.mal.automation.scheduler.db.CronExpression;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
class CronExpressionResourceAssembler implements ResourceAssembler<CronExpression, Resource<CronExpression>> {


    @Override
    public Resource<CronExpression> toResource(CronExpression cronExpression) {
        return new Resource<>(cronExpression,
                linkTo(methodOn(CronExpressionController.class).retrieve(cronExpression.getId()))
                        .withSelfRel(),
                linkTo(methodOn(CronExpressionController.class).retrieveAll()).withRel("cronexpressions"));
    }

}
