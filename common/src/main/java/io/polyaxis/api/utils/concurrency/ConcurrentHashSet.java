package io.polyaxis.api.utils.concurrency;

import jakarta.annotation.Nonnull;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/// Concurrent Hash Set.
///
/// @author github.com/MoritzArena
/// @date 2025/07/05
/// @since 1.0
public class ConcurrentHashSet<E> extends AbstractSet<E> {

    private final ConcurrentHashMap<E, Boolean> map;

    public ConcurrentHashSet() {
        super();
        this.map = new ConcurrentHashMap<>();
    }

    public ConcurrentHashSet(final int size) {
        super();
        this.map = new ConcurrentHashMap<>(size);
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(Object o) {
        return this.map.containsKey((E) o);
    }

    @Override
    @Nonnull
    public Iterator<E> iterator() {
        return this.map.keySet().iterator();
    }

    @Override
    public boolean add(E o) {
        return this.map.putIfAbsent(o, Boolean.TRUE) == null;
    }

    @Override
    public boolean remove(Object o) {
        return this.map.remove(o) != null;
    }

    @Override
    public void clear() {
        this.map.clear();
    }
}
