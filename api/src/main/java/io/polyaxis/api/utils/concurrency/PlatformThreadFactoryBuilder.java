package io.polyaxis.api.utils.concurrency;

import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/// Build platform thread factory.
///
/// @author github.com/MoritzArena
/// @date 2025/07/05
/// @since 1.0
public final class PlatformThreadFactoryBuilder {

    private static final String THREAD_CONCAT_ID_PLACEHOLDER = "-%d";
    
    /// Whether it is a daemon thread.
    private Boolean daemon = false;
    
    /// Thread priority.
    private Integer priority = null;
    
    /// Thread name template.
    private String nameFormat = null;
    
    /// Uncaught exception handler.
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler = null;

    /// Customize thread factory.
    private ThreadFactory customizeFactory = null;
    
    /// set nameFormat property.
    public PlatformThreadFactoryBuilder nameFormat(String nameFormat) {
        checkNullParameter(nameFormat, "nameFormat cannot be null.");
        if (!nameFormat.endsWith(THREAD_CONCAT_ID_PLACEHOLDER)) { // concat thread id
            nameFormat = nameFormat.concat(THREAD_CONCAT_ID_PLACEHOLDER);
        }
        this.nameFormat = nameFormat;
        return this;
    }
    
    /// set priority property.
    public PlatformThreadFactoryBuilder priority(int priority) {
        if (priority > Thread.MAX_PRIORITY || priority < Thread.MIN_PRIORITY) {
            throw new IllegalArgumentException(
                    String.format("The value of priority should be between %s and %s",
                            Thread.MIN_PRIORITY + 1, Thread.MAX_PRIORITY + 1)
            );
        }
        this.priority = priority;
        return this;
    }
    
    /// set uncaughtExceptionHandler property.
    public PlatformThreadFactoryBuilder uncaughtExceptionHandler(
            Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        checkNullParameter(uncaughtExceptionHandler, "uncaughtExceptionHandler cannot be null.");
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
        return this;
    }
    
    /// set daemon property.
    public PlatformThreadFactoryBuilder daemon(boolean daemon) {
        this.daemon = daemon;
        return this;
    }
    
    /// set customizeFactory property.
    public PlatformThreadFactoryBuilder customizeFactory(ThreadFactory factory) {
        checkNullParameter(factory, "factory cannot be null.");
        this.customizeFactory = factory;
        return this;
    }
    
    /// build thread factory.
    public ThreadFactory build() {
        ThreadFactory factory = customizeFactory == null ? Executors.defaultThreadFactory() : customizeFactory;
        final AtomicLong count = (nameFormat != null) ? new AtomicLong(0) : null;
        return r -> {
            final Thread thread = factory.newThread(r);
            if (nameFormat != null) {
                thread.setName(format(nameFormat, count.getAndIncrement()));
            }
            if (priority != null) {
                thread.setPriority(priority);
            }
            if (uncaughtExceptionHandler != null) {
                thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
            }
            thread.setDaemon(daemon);
            return thread;
        };
    }
    
    private String format(String format, Object... args) {
        return String.format(Locale.ROOT, format, args);
    }
    
    private void checkNullParameter(Object obj, String msg) {
        if (obj == null) {
            throw new IllegalArgumentException(msg);
        }
    }
}
