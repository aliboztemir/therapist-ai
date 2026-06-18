package io.therapistai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Enables Spring's {@code @Async} annotation support with MDC context propagation.
 *
 * <p>Registers a {@link ThreadPoolTaskExecutor} decorated with {@link MdcTaskDecorator}
 * so that async event handlers (e.g. {@code AnalyticsEventHandler}) inherit the
 * {@code traceId} from the originating request thread.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setTaskDecorator(new MdcTaskDecorator());
        executor.setThreadNamePrefix("async-");
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(100);
        executor.initialize();
        return executor;
    }
}
