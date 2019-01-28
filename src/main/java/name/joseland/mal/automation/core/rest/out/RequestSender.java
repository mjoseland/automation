package name.joseland.mal.automation.core.rest.out;

import java.net.http.HttpResponse;
import java.util.concurrent.Callable;

public interface RequestSender<T extends HttpResponse<U>, U> extends Callable<T> {

}
