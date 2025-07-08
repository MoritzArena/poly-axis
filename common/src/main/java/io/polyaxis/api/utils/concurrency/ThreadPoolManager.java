package io.polyaxis.api.utils.concurrency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/// Thread Pool Manager.
///
/// @author github.com/MoritzArena
/// @date 2025/07/05
/// @since 1.0
public class ThreadPoolManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadPoolManager.class);

    private Map<String, Map<String, Set<ExecutorService>>> resourcesManager;

    private static final ThreadPoolManager INSTANCE = new ThreadPoolManager();

    private static final AtomicBoolean CLOSED = new AtomicBoolean(false);

    static {
        INSTANCE.init();
        ThreadUtils.addShutdownHook(new Thread(() -> {
            LOGGER.warn("[ThreadPoolManager] Start destroying ThreadPool");
            shutdown();
            LOGGER.warn("[ThreadPoolManager] Destruction of the end");
        }));
    }

    public static ThreadPoolManager getInstance() {
        return INSTANCE;
    }

    private ThreadPoolManager() {
    }

    private void init() {
        resourcesManager = new ConcurrentHashMap<>(8);
    }

    /// Register the thread pool resources with the resource manager.
    ///
    /// @param namespace namespace name
    /// @param group     group name
    /// @param executor  [ExecutorService]
    public void register(String namespace, String group, ExecutorService executor) {
        resourcesManager.compute(namespace, (namespaceKey, map) -> {
            if (map == null) {
                map = new HashMap<>(8);
            }
            map.computeIfAbsent(group, groupKey -> new HashSet<>()).add(executor);
            return map;
        });
    }

    /// Cancel the uniform lifecycle management for all threads under this resource.
    ///
    /// @param namespace namespace name
    /// @param group     group name
    public void deregister(String namespace, String group) {
        resourcesManager.computeIfPresent(namespace, (key, map) -> {
            map.remove(group);
            return map;
        });
    }

    /// Undoing the uniform lifecycle management of [ExecutorService] under this resource.
    ///
    /// @param namespace namespace name
    /// @param group     group name
    /// @param executor  [ExecutorService]
    public void deregister(String namespace, String group, ExecutorService executor) {
        resourcesManager.computeIfPresent(namespace, (namespaceKey, map) -> {
            map.computeIfPresent(group, (groupKey, set) -> {
                set.remove(executor);
                return set;
            });
            return map;
        });
    }

    /// Destroys all thread pool resources under this namespace.
    ///
    /// @param namespace namespace
    public void destroy(final String namespace) {
        Map<String, Set<ExecutorService>> map = resourcesManager.remove(namespace);
        if (map != null) {
            for (Set<ExecutorService> set : map.values()) {
                for (ExecutorService executor : set) {
                    ThreadUtils.shutdownThreadPool(executor);
                }
                set.clear();
            }
            map.clear();
        }
    }

    /// This namespace destroys all thread pool resources under the grouping.
    ///
    /// @param namespace namespace
    /// @param group     group
    public void destroy(final String namespace, final String group) {
        resourcesManager.computeIfPresent(namespace, (namespaceKey, map) -> {
            map.computeIfPresent(group, (groupKey, set) -> {
                for (ExecutorService executor : set) {
                    ThreadUtils.shutdownThreadPool(executor);
                }
                set.clear();
                return null;
            });
            return map;
        });
    }

    /// Shutdown thread pool manager.
    public static void shutdown() {
        if (!CLOSED.compareAndSet(false, true)) {
            return;
        }
        Set<String> namespaces = INSTANCE.resourcesManager.keySet();
        for (String namespace : namespaces) {
            INSTANCE.destroy(namespace);
        }
    }

    public Map<String, Map<String, Set<ExecutorService>>> getResourcesManager() {
        return resourcesManager;
    }
}
