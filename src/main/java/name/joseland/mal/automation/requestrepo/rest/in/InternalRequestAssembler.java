package name.joseland.mal.automation.requestrepo.rest.in;

import name.joseland.mal.automation.requestrepo.db.InternalRequest;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
class InternalRequestAssembler implements ResourceAssembler<InternalRequest, Resource<InternalRequest>> {

    @Override
    public Resource<InternalRequest> toResource(InternalRequest internalRequest) {
        return new Resource<>(internalRequest,
                linkTo(methodOn(InternalRequestController.class).retrieve(internalRequest.getId())).withSelfRel(),
                linkTo(methodOn(InternalRequestController.class).retrieveAll()).withRel("internal-requests"));
    }

}
