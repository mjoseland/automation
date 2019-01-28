package name.joseland.mal.automation.core.rest.out.internal;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * Assembles a {@link URI} from an ID of an internal service and a resource path.
 */
@Component
@Scope(value = "singleton")
public class InternalRequestUriAssembler {

    // TODO
    public URI fromResourcePath(String resourceId, String resourcePath) {
        // get resource IP from its ID

        // assemble and return the URI
        return null;
    }


}
