package name.joseland.mal.automation.scheduler.rest.in;

import name.joseland.mal.automation.core.rest.in.exception.ResourceNotFoundException;
import name.joseland.mal.automation.scheduler.db.TriggerConfig;
import name.joseland.mal.automation.scheduler.db.TriggerConfigRepository;
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
public class TriggerConfigController {

    private final TriggerConfigRepository repository;
    private final TriggerConfigAssembler resourceAssembler;

    TriggerConfigController(@Autowired TriggerConfigRepository repository,
                            @Autowired TriggerConfigAssembler resourceAssembler) {
        this.repository = repository;
        this.resourceAssembler = resourceAssembler;
    }


    /* ********************************************************************************************************** */
    /* ********************************************* PUBLIC METHODS ********************************************* */
    /* ********************************************************************************************************** */

    @GetMapping("/trigger-configs")
    public Resources<Resource<TriggerConfig>> retrieveAll() {
        List<Resource<TriggerConfig>> triggerConfigResources = repository.findAll().stream()
                .map(resourceAssembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(triggerConfigResources,
                linkTo(methodOn(TriggerConfigController.class).retrieveAll()).withSelfRel());
    }

    @PostMapping("/trigger-configs")
    public ResponseEntity<Resource<TriggerConfig>> create(@RequestBody TriggerConfig triggerConfig)
            throws URISyntaxException {
        Resource<TriggerConfig> triggerConfigResource = resourceAssembler.toResource(repository.save(triggerConfig));

        return ResponseEntity
                .created(new URI(triggerConfigResource.getId().expand().getHref()))
                .body(triggerConfigResource);
    }

    @GetMapping("/trigger-configs/{id}")
    public Resource<TriggerConfig> retrieve(@PathVariable int id) {
        Optional<TriggerConfig> triggerConfigOpt = repository.findById(id);

        if (triggerConfigOpt.isEmpty())
            throw new ResourceNotFoundException("TriggerConfig", id);

        return resourceAssembler.toResource(triggerConfigOpt.get());
    }

    @PutMapping("/trigger-configs/{id}")
    public ResponseEntity<Resource<TriggerConfig>> update(@RequestBody TriggerConfig newTriggerConfig,
                                    @PathVariable int id) throws URISyntaxException {
        TriggerConfig savedTriggerConfig = repository.findById(id)
                .map(triggerConfig -> {
                    triggerConfig.setType(newTriggerConfig.getType());
                    triggerConfig.setCronExpression(newTriggerConfig.getCronExpression());

                    return repository.save(triggerConfig);
                }).orElseGet(() -> {
                    newTriggerConfig.setId(id);

                    return repository.save(newTriggerConfig);
                });

        Resource<TriggerConfig> resource = resourceAssembler.toResource(savedTriggerConfig);

        return ResponseEntity.created(new URI(resource.getId().expand().getHref()))
                .body(resource);
    }

    // TODO handle TriggerConfigs that are still referenced by a ScheduledRequestConfig
    @DeleteMapping("/trigger-configs/{id}")
    public ResponseEntity delete(@PathVariable int id) {
        Optional<TriggerConfig> triggerConfigOpt = repository.findById(id);

        if (triggerConfigOpt.isEmpty())
            throw new ResourceNotFoundException("TriggerConfig", id);

        repository.delete(triggerConfigOpt.get());

        return ResponseEntity.noContent().build();
    }

}
