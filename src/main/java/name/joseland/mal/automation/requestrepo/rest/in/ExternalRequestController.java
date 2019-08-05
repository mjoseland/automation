package name.joseland.mal.automation.requestrepo.rest.in;

import name.joseland.mal.automation.core.rest.in.exception.ResourceNotFoundException;
import name.joseland.mal.automation.requestrepo.db.ExternalRequest;
import name.joseland.mal.automation.requestrepo.db.ExternalRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class ExternalRequestController {


	private final ExternalRequestRepository repository;
	private final ExternalRequestAssembler resourceAssembler;


	ExternalRequestController(@Autowired ExternalRequestRepository repository,
			@Autowired ExternalRequestAssembler resourceAssembler) {
		this.repository = repository;
		this.resourceAssembler = resourceAssembler;
	}


	/* ********************************************************************************************************** */
	/* ********************************************* PUBLIC METHODS ********************************************* */
	/* ********************************************************************************************************** */


	@GetMapping("/external-requests")
	public Resources<Resource<ExternalRequest>> retrieveAll() {
		List<Resource<ExternalRequest>> externalRequestResources = repository.findAll().stream()
				.map(resourceAssembler::toResource)
				.collect(Collectors.toList());

		return new Resources<>(externalRequestResources,
				linkTo(methodOn(ExternalRequestController.class).retrieveAll()).withSelfRel());
	}

	@PostMapping("/external-requests")
	public ResponseEntity<Resource<ExternalRequest>> create(@RequestBody ExternalRequest externalRequest) {
		ExternalRequest newExternalRequest = repository.save(externalRequest);

		return ResponseEntity
				.created(linkTo(methodOn(ExternalRequestController.class).retrieve(newExternalRequest.getId())).toUri())
				.body(resourceAssembler.toResource(newExternalRequest));
	}

	@GetMapping("/external-requests/{id}")
	public Resource<ExternalRequest> retrieve(@PathVariable int id) {
		Optional<ExternalRequest> externalRequestOpt = repository.findById(id);

		if (externalRequestOpt.isEmpty())
			throw new ResourceNotFoundException("ExternalRequest", id);

		return resourceAssembler.toResource(externalRequestOpt.get());
	}

	@PutMapping("external-requests/{id}")
	public ResponseEntity<?> update(@RequestBody ExternalRequest newExternalRequest,
			@PathVariable int id) throws URISyntaxException {
		ExternalRequest savedExternalRequest = repository.findById(id)
				.map(externalRequest -> {
					externalRequest.setDescription(newExternalRequest.getDescription());
					externalRequest.setHttpMethod(newExternalRequest.getHttpMethod());
					externalRequest.setUrl(newExternalRequest.getUrl());
					externalRequest.getHeaderFields().clear();
					externalRequest.getHeaderFields().putAll(newExternalRequest.getHeaderFields());
					externalRequest.setBody(newExternalRequest.getBody());

					return repository.save(externalRequest);
				}).orElseGet(() -> {
					newExternalRequest.setId(id);

					return repository.save(newExternalRequest);
				});

		Resource<ExternalRequest> savedExternalRequestResource = resourceAssembler.toResource(savedExternalRequest);

		return ResponseEntity
				.created(new URI(savedExternalRequestResource.getId().expand().getHref()))
				.body(resourceAssembler.toResource(savedExternalRequest));
	}

	@DeleteMapping("external-requests/{id}")
	public ResponseEntity<?> delete(@PathVariable int id) {
		Optional<ExternalRequest> externalRequestOpt = repository.findById(id);

		if (externalRequestOpt.isEmpty())
			throw new ResourceNotFoundException("ExternalRequest", id);

		repository.delete(externalRequestOpt.get());

		return ResponseEntity.noContent().build();
	}

}
