package name.joseland.mal.automation.requestrepo.rest.in;

import name.joseland.mal.automation.requestrepo.db.InternalRequest;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;

public class InternalRequestResourceAssembler implements ResourceAssembler<InternalRequest, Resource<InternalRequest>> {

    @Override
    public Resource<InternalRequest> toResource(InternalRequest entity) {
        // TODO
        return null;
    }

}
