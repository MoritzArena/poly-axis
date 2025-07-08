package io.polyaxis.api.utils.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/// Triple object.
///
/// @author github.com/MoritzArena
/// @date 2025/07/05
/// @since 1.0
public final class Triple<T, R, S> {
    private final T first;

    private final R second;

    private final S third;

    private Triple(
            final T first,
            final R second,
            final S third
    ) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @JsonCreator
    public static <T, R, S> Triple<T, R, S> of(
            @JsonProperty("first") final T first,
            @JsonProperty("second") final R second,
            @JsonProperty("third") final S third
    ) {
        return new Triple<>(first, second, third);
    }

    public T getFirst() {
        return first;
    }

    public R getSecond() {
        return second;
    }

    public S getThird() {
        return third;
    }

    /**
     * contains any object.
     */
    public boolean contains(final Object a) {
        return Objects.equals(first, a)
                || Objects.equals(second, a)
                || Objects.equals(third, a);
    }

    @Override
    public String toString() {
        return "Triple{first=" + first + ", second=" + second + ", third=" + third + '}';
    }

    @Override
    public int hashCode() {
        return (17 * this.first.hashCode())
                + (19 * this.second.hashCode())
                + (21 * this.third.hashCode());
    }
}
