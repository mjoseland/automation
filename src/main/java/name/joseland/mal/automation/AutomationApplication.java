package name.joseland.mal.automation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class AutomationApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutomationApplication.class, args);
    }

}

