package io.polyaxis.api.utils.documentation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// [Unsafe].
///
/// @author github.com/MoritzArena
/// @date 2025/07/05
/// @since 1.0
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR})
public @interface Unsafe {
}
