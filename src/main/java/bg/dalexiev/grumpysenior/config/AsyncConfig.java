package bg.dalexiev.grumpysenior.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean
    public ThreadPoolTaskExecutor agentRunExecutor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);   // Always keep 20 threads ready
        executor.setMaxPoolSize(100);  // Scale up to 100 under load
        executor.setQueueCapacity(500); // Wait in line if all 100 are busy
        executor.setThreadNamePrefix("agent-worker-");
        executor.initialize();
        return executor;
    }

}
