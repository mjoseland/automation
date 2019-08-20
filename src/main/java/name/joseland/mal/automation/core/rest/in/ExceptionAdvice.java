package name.joseland.mal.automation.core.rest.in;

import name.joseland.mal.automation.core.rest.in.exception.ResourceNotFoundException;
import name.joseland.mal.automation.core.rest.out.internal.exception.InternalServiceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
class ExceptionAdvice {

    @ResponseBody
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String resourceNotFoundHandler(ResourceNotFoundException e) {
        return e.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(InternalServiceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String internalServiceNotFoundHandler(InternalServiceNotFoundException e) {
        return e.getMessage();
    }

}
