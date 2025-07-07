package io.polyaxis.api.utils.concurrency;

import io.polyaxis.api.utils.context.PropertyUtils;
import org.slf4j.Logger;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/// Thread Utils.
///
/// @author github.com/MoritzArena
/// @date 2025/07/05
/// @since 1.0
public final class ThreadUtils {

    private static final int THREAD_MULTIPLES = 2;
    
    private ThreadUtils() {
    }
    
    /// Sleep.
    ///
    /// @param millis sleep millisecond
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /// Await count down latch.
    ///
    /// @param latch count down latch
    public static void latchAwait(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /// Await count down latch with timeout.
    ///
    /// @param latch count down latch
    /// @param time  timeout time
    /// @param unit  time unit
    public static void latchAwait(CountDownLatch latch, long time, TimeUnit unit) {
        try {
            latch.await(time, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /// Through the number of cores, calculate the appropriate number of threads; 1.5-2 times the number of CPU cores.
    ///
    /// @return thread count
    public static int getSuitableThreadCount() {
        return getSuitableThreadCount(THREAD_MULTIPLES);
    }
    
    /// Through the number of cores, calculate the appropriate number of threads.
    ///
    /// Improve CPU cache rate.
    ///
    /// @param threadMultiple multiple time of cores
    /// @return thread count
    public static int getSuitableThreadCount(int threadMultiple) {
        final int coreCount = PropertyUtils.getProcessorsCount();
        int workerCount = 1;
        while (workerCount < coreCount * threadMultiple) {
            workerCount <<= 1;
        }
        return workerCount;
    }
    
    public static void shutdownThreadPool(ExecutorService executor) {
        shutdownThreadPool(executor, null);
    }
    
    /// Shutdown thread pool.
    ///
    /// @param executor thread pool
    /// @param logger   logger
    public static void shutdownThreadPool(ExecutorService executor, Logger logger) {
        executor.shutdown();
        int retry = 3;
        while (retry > 0) {
            retry--;
            try {
                if (executor.awaitTermination(100, TimeUnit.MILLISECONDS)) {
                    return;
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.interrupted();
            } catch (Throwable ex) {
                if (logger != null) {
                    logger.error("ThreadPoolManager shutdown executor has error : ", ex);
                }
            }
        }
        executor.shutdownNow();
    }

    /// Link provided runnable to global shutdown hook.
    public static void addShutdownHook(Runnable runnable) {
        Runtime.getRuntime().addShutdownHook(new Thread(runnable));
    }
}
