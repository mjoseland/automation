package name.joseland.mal.automation.requestrepo.rest.in;

import name.joseland.mal.automation.requestrepo.db.InternalRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class InternalRequestController {

    private final InternalRequestRepository repository;

    private final InternalRequestResourceAssembler resourceAssembler;


    InternalRequestController(@Autowired InternalRequestRepository repository,
                        @Autowired InternalRequestResourceAssembler resourceAssembler) {
        this.repository = repository;
        this.resourceAssembler = resourceAssembler;
    }


    /* ********************************************************************************************************** */
    /* ********************************************* PUBLIC METHODS ********************************************* */
    /* ********************************************************************************************************** */



}
