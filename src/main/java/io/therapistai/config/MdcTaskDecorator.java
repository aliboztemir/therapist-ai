package io.therapistai.config;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

import java.util.Map;

/**
 * {@link TaskDecorator} that copies the calling thread's MDC context map into
 * each async task thread, ensuring that {@code traceId} and other MDC values
 * are available in {@code @Async} event handlers and scheduled tasks.
 *
 * <p>Without this decorator, async threads start with an empty MDC, causing
 * {@code traceId=no-trace} in log lines produced by async handlers.
 */
public class MdcTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        // Capture MDC from the calling (request) thread
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        return () -> {
            try {
                if (mdcContext != null) {
                    MDC.setContextMap(mdcContext);
                }
                runnable.run();
            } finally {
                MDC.clear();
            }
        };
    }
}

