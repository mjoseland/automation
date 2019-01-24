package name.joseland.mal.automation.requestrepo.rest.in;

import name.joseland.mal.automation.core.rest.in.exception.ResourceNotFoundException;
import name.joseland.mal.automation.requestrepo.db.InternalRequest;
import name.joseland.mal.automation.requestrepo.db.InternalRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class InternalRequestController {

    private final InternalRequestRepository repository;
    private final InternalRequestAssembler resourceAssembler;


    InternalRequestController(@Autowired InternalRequestRepository repository,
                              @Autowired InternalRequestAssembler resourceAssembler) {
        this.repository = repository;
        this.resourceAssembler = resourceAssembler;
    }


    /* ********************************************************************************************************** */
    /* ********************************************* PUBLIC METHODS ********************************************* */
    /* ********************************************************************************************************** */


    @GetMapping("/internal-requests")
    public Resources<Resource<InternalRequest>> retrieveAll() {
        List<Resource<InternalRequest>> internalRequestResources = repository.findAll().stream()
                .map(resourceAssembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(internalRequestResources,
                linkTo(methodOn(InternalRequestController.class).retrieveAll()).withSelfRel());
    }

    @PostMapping("/internal-requests")
    public ResponseEntity<Resource<InternalRequest>> create(@RequestBody InternalRequest internalRequest) {
        InternalRequest newInternalRequest = repository.save(internalRequest);

        return ResponseEntity
                .created(linkTo(methodOn(InternalRequestController.class).retrieve(newInternalRequest.getId())).toUri())
                .body(resourceAssembler.toResource(newInternalRequest));
    }

    @GetMapping("/internal-requests/{id}")
    public Resource<InternalRequest> retrieve(@PathVariable int id) {
        Optional<InternalRequest> internalRequestOpt = repository.findById(id);

        if (internalRequestOpt.isEmpty())
            throw new ResourceNotFoundException("InternalRequest", id);

        return resourceAssembler.toResource(internalRequestOpt.get());
    }

    @PutMapping("internal-requests/{id}")
    public ResponseEntity<?> update(@RequestBody InternalRequest newInternalRequest,
                                    @PathVariable int id) throws URISyntaxException {
        InternalRequest savedInternalRequest = repository.findById(id)
                .map(internalRequest -> {
                    internalRequest.setHttpMethod(newInternalRequest.getHttpMethod());
                    internalRequest.setServiceId(newInternalRequest.getServiceId());
                    internalRequest.setResource(newInternalRequest.getResource());
                    internalRequest.setBody(newInternalRequest.getBody());

                    return repository.save(internalRequest);
                }).orElseGet(() -> {
                    newInternalRequest.setId(id);

                    return repository.save(newInternalRequest);
                });

        Resource<InternalRequest> savedInternalRequestResource = resourceAssembler.toResource(savedInternalRequest);

        return ResponseEntity
                .created(new URI(savedInternalRequestResource.getId().expand().getHref()))
                .body(savedInternalRequest);
    }

    @DeleteMapping("internal-requests/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        Optional<InternalRequest> internalRequestOpt = repository.findById(id);

        if (internalRequestOpt.isEmpty())
            throw new ResourceNotFoundException("InternalRequest", id);

        repository.delete(internalRequestOpt.get());

        return ResponseEntity.noContent().build();
    }

}
