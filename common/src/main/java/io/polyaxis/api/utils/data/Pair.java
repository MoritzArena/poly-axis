package io.polyaxis.api.utils.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/// Pair object.
///
/// @author github.com/MoritzArena
/// @date 2025/07/05
/// @since 1.0
public final class Pair<A, B> {
    
    private A first;
    
    private B second;

    Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    @JsonCreator
    public static <A, B> Pair<A, B> of(
            @JsonProperty("first") final A first,
            @JsonProperty("second") final B second
    ) {
        return new Pair<>(first, second);
    }

    public void setFirst(A first) {
        this.first = first;
    }

    public void setSecond(B second) {
        this.second = second;
    }

    public A getFirst() {
        return first;
    }
    
    public B getSecond() {
        return second;
    }

    @JsonIgnore
    public boolean isFull() {
        return first != null && second != null;
    }

    public boolean contains(final Object a) {
        return Objects.equals(first, a) || Objects.equals(second, a);
    }

    @Override
    public String toString() {
        return "Pair{first=" + first + ", second=" + second + '}';
    }

    @Override
    public int hashCode() {
        return (17 * this.first.hashCode())
                + (19 * this.second.hashCode());
    }
}
