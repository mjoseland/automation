package name.joseland.mal.automation.scheduler.rest.in;

import name.joseland.mal.automation.core.rest.in.exception.ResourceNotFoundException;
import name.joseland.mal.automation.scheduler.db.ScheduledRequestConfig;
import name.joseland.mal.automation.scheduler.db.ScheduledRequestConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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
public class ScheduledRequestConfigController {

    private final ScheduledRequestConfigRepository repository;
    private final ScheduledRequestConfigAssembler resourceAssembler;

    ScheduledRequestConfigController(@Autowired ScheduledRequestConfigRepository repository,
                                     @Autowired ScheduledRequestConfigAssembler resourceAssembler) {
        this.repository = repository;
        this.resourceAssembler = resourceAssembler;
    }


    /* ********************************************************************************************************** */
    /* ********************************************* PUBLIC METHODS ********************************************* */
    /* ********************************************************************************************************** */


    @GetMapping("/scheduled-request-configs")
    public Resources<Resource<ScheduledRequestConfig>> retrieveAll() {
        List<Resource<ScheduledRequestConfig>> scheduledRequestConfigResources = repository.findAll().stream()
                .map(resourceAssembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(scheduledRequestConfigResources,
                linkTo(methodOn(ScheduledRequestConfigController.class).retrieveAll()).withSelfRel());
    }

    @PostMapping("/scheduled-request-configs")
    public ResponseEntity<?> create(@RequestBody ScheduledRequestConfig scheduledRequestConfig)
            throws URISyntaxException {
        Resource<ScheduledRequestConfig> scheduledRequestConfigResource =
                resourceAssembler.toResource(repository.save(scheduledRequestConfig));

        return ResponseEntity
                .created(new URI(scheduledRequestConfigResource.getId().expand().getHref()))
                .body(scheduledRequestConfigResource);
    }

    @GetMapping("/scheduled-request-configs/{id}")
    public Resource<ScheduledRequestConfig> retrieve(@PathVariable int id) {
        Optional<ScheduledRequestConfig> scheduledRequestConfigOpt = repository.findById(id);

        if (scheduledRequestConfigOpt.isEmpty())
            throw new ResourceNotFoundException("ScheduledRequestConfig", id);

        return resourceAssembler.toResource(scheduledRequestConfigOpt.get());
    }

    @PutMapping("/scheduled-request-configs/{id}")
    public ResponseEntity<?> update(@RequestBody ScheduledRequestConfig newScheduledRequestConfig,
                                    @PathVariable int id) throws URISyntaxException {
        ScheduledRequestConfig savedScheduledRequestConfig = repository.findById(id)
                .map(scheduledRequestConfig -> {
                    scheduledRequestConfig.setTriggerConfig(newScheduledRequestConfig.getTriggerConfig());
                    scheduledRequestConfig.setRequestType(newScheduledRequestConfig.getRequestType());
                    scheduledRequestConfig.setRequestId(newScheduledRequestConfig.getRequestId());

                    return repository.save(scheduledRequestConfig);
                }).orElseGet(() -> {
                    newScheduledRequestConfig.setId(id);

                    return repository.save(newScheduledRequestConfig);
                });

        Resource<ScheduledRequestConfig> scheduledRequestConfigResource =
                resourceAssembler.toResource(savedScheduledRequestConfig);

        return ResponseEntity.created(new URI(scheduledRequestConfigResource.getId().expand().getHref()))
                .body(scheduledRequestConfigResource);
    }

    @DeleteMapping("/scheduled-request-configs/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        try {
            repository.deleteById(id);
//            triggerConfigRepository.delete();
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("ScheduledRequestConfig", id);
        }

        return ResponseEntity.noContent().build();
    }

}
