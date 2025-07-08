package io.polyaxis.api.utils.concurrency;

import io.polyaxis.api.utils.misc.StringUtils;
import jakarta.annotation.Nonnull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/// Named thread factory.
///
/// @author github.com/MoritzArena
/// @date 2025/07/05
/// @since 1.0
public class NamedThreadFactory implements ThreadFactory {
    
    private final AtomicInteger id = new AtomicInteger(0);
    
    private final String name;
    
    public NamedThreadFactory(String name) {
        if (!name.endsWith(StringUtils.DOT)) {
            name += StringUtils.DOT;
        }
        this.name = name;
    }
    
    @Override
    public Thread newThread(@Nonnull Runnable runnable) {
        String threadName = name + id.getAndIncrement();
        Thread thread = new Thread(runnable, threadName);
        thread.setDaemon(true);
        return thread;
    }

}
