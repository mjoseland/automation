package name.joseland.mal.automation.requestrepo.rest.in;

import name.joseland.mal.automation.requestrepo.db.ExternalRequest;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class ExternalRequestAssembler implements ResourceAssembler<ExternalRequest, Resource<ExternalRequest>> {

	@Override
	public Resource<ExternalRequest> toResource(ExternalRequest externalRequest) {
		return new Resource<>(externalRequest,
				linkTo(methodOn(ExternalRequestController.class).retrieve(externalRequest.getId())).withSelfRel(),
				linkTo(methodOn(ExternalRequestController.class).retrieveAll()).withRel("external-requests"));
	}

}
