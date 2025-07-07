package io.polyaxis.api.utils.concurrency;

import io.polyaxis.api.utils.data.FixedSizePriorityQueue;
import io.polyaxis.api.utils.data.Pair;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/// Thread safe max heap data structure.
///
/// @param <T> Element type
///
/// @author github.com/MoritzArena
/// @date 2025/07/05
/// @since 1.0
public class TopNCounter<T> {

    private final Comparator<Pair<String, AtomicInteger>> comparator;

    protected ConcurrentMap<T, AtomicInteger> dataCount;

    public TopNCounter() {
        dataCount = new ConcurrentHashMap<>();
        this.comparator = Comparator.comparingInt(value -> value.getSecond().get());
    }

    /// Get topN counter by PriorityQueue.
    ///
    /// @param topN topN
    /// @return topN counter
    public List<Pair<String, AtomicInteger>> getCounterOfTopN(int topN) {
        ConcurrentMap<T, AtomicInteger> snapshot = dataCount;
        dataCount = new ConcurrentHashMap<>(1);
        FixedSizePriorityQueue<Pair<String, AtomicInteger>> queue = new FixedSizePriorityQueue<>(topN, comparator);
        for (T t : snapshot.keySet()) {
            queue.offer(Pair.of(t.toString(), snapshot.get(t)));
        }
        return queue.toList();
    }

    /// Increment 1 count for target key.
    ///
    /// @param t key
    public void increment(T t) {
        increment(t, 1);
    }

    /// Increment specified count for target key.
    ///
    /// @param t     key
    /// @param count count
    public void increment(T t, int count) {
        dataCount.computeIfAbsent(t, k -> new AtomicInteger(0)).addAndGet(count);
    }

    /// Directly set count for target key.
    ///
    /// @param t     key
    /// @param count new count
    public void set(T t, int count) {
        dataCount.computeIfAbsent(t, k -> new AtomicInteger(0)).set(count);
    }

    public void reset() {
        dataCount.clear();
    }
}
