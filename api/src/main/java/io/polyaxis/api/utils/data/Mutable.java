package io.polyaxis.api.utils.data;

/// Mutable. copy from common-lang3.
///
/// @author github.com/MoritzArena
/// @date 2025/07/05
/// @since 1.0
public interface Mutable<T> {

    T getValue();

    void setValue(T value);
}
