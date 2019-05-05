package name.joseland.mal.automation;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application-docker.properties")
@Profile("docker")
public class DockerConfiguration {
}
