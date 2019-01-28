package name.joseland.mal.automation.scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class BeanConfiguration {

    @Value("${automation.scheduler.task-scheduler.pool-size}")
    private Integer taskSchedulerPoolSize;


    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();

        taskScheduler.setPoolSize(taskSchedulerPoolSize);
        taskScheduler.setThreadNamePrefix("taskScheduler");

        return taskScheduler;
    }
}
