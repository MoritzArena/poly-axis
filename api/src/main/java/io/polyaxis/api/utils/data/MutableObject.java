package io.polyaxis.api.utils.data;

import java.io.Serial;
import java.io.Serializable;

/// MutableObject. copy from common-lang3.
///
/// @author github.com/MoritzArena
/// @date 2025/07/05
/// @since 1.0
public class MutableObject<T> implements Mutable<T>, Serializable {

    @Serial
    private static final long serialVersionUID = 86241875189L;

    private T value;

    public MutableObject() {}

    public MutableObject(final T value) {
        this.value = value;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void setValue(final T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
