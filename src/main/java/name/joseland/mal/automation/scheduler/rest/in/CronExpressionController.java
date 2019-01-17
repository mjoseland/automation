package name.joseland.mal.automation.scheduler.rest.in;

import name.joseland.mal.automation.scheduler.db.CronExpression;
import name.joseland.mal.automation.scheduler.db.CronExpressionRepository;
import name.joseland.mal.automation.core.rest.in.exception.ResourceNotFoundException;
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
@RequestMapping
class CronExpressionController {

    private final CronExpressionRepository repository;
    private final CronExpressionResourceAssembler resourceAssembler;

    CronExpressionController(@Autowired CronExpressionRepository repository,
                             @Autowired CronExpressionResourceAssembler resourceAssembler) {
        this.repository = repository;
        this.resourceAssembler = resourceAssembler;
    }


    /* ********************************************************************************************************** */
    /* ********************************************* PUBLIC METHODS ********************************************* */
    /* ********************************************************************************************************** */


    @GetMapping("/cron-expressions")
    public Resources<Resource<CronExpression>> retrieveAll() {
        List<Resource<CronExpression>> cronExpressions = repository.findAll().stream()
                .map(resourceAssembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(cronExpressions,
                linkTo(methodOn(CronExpressionController.class).retrieveAll()).withSelfRel());
    }

    @PostMapping("/cron-expressions")
    public ResponseEntity<?> create(@RequestBody CronExpression cronExpression) throws URISyntaxException {
        Resource<CronExpression> resource = resourceAssembler.toResource(repository.save(cronExpression));

        return ResponseEntity
                .created(new URI(resource.getId().expand().getHref()))
                .body(resource);
    }

    @GetMapping("/cron-expressions/{id}")
    public Resource<CronExpression> retrieve(@PathVariable int id) {
        Optional<CronExpression> cronExpressionOpt = repository.findById(id);

        if (cronExpressionOpt.isEmpty())
            throw new ResourceNotFoundException("CronExpression", id);

        return resourceAssembler.toResource(cronExpressionOpt.get());
    }

    @PutMapping("/cron-expressions/{id}")
    public ResponseEntity<?> update(@RequestBody CronExpression newCronExpression,
                                    @PathVariable int id) throws URISyntaxException {
        CronExpression savedCronExpression = repository.findById(id)
                .map(cronExpression -> {
                    cronExpression.setExpression(newCronExpression.getExpression());
                    cronExpression.setDescription(newCronExpression.getDescription());

                    return repository.save(cronExpression);
                }).orElseGet(() -> {
                    newCronExpression.setId(id);

                    return repository.save(newCronExpression);
                });

        Resource<CronExpression> resource = resourceAssembler.toResource(savedCronExpression);

        return ResponseEntity
                .created(new URI(resource.getId().expand().getHref()))
                .body(resource);
    }

    @DeleteMapping("/cron-expressions/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

}
