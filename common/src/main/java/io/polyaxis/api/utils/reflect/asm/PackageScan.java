package io.polyaxis.api.utils.reflect.asm;

import java.lang.annotation.Annotation;
import java.util.Set;

/// Interface PackageScan.
///
/// @author github.com/MoritzArena
/// @date 2025/07/05
/// @since 1.0
public interface PackageScan {

    /// Scan all appropriate Class object through the package name and Class object.
    ///
    /// @param pkg          package name, exp, com.inovance.platform
    /// @param requestClass super class
    /// @param <T>          Class type
    /// @return a set contains Class
    <T> Set<Class<T>> getSubTypesOf(
            final String pkg, Class<T> requestClass);

    /// Scan first level appropriate Class object through the package name and annotation.
    ///
    /// @param pkg        package name, exp, com.inovance.platform
    /// @param annotation annotation
    /// @param <T>        Class type
    /// @return a set contains Class object
    <T> Set<Class<T>> getTypesAnnotatedWith(
            final String pkg, Class<? extends Annotation> annotation);

    /// Scan all appropriate Class object through the package name and annotation.
    ///
    /// @param pkg        package name, exp, com.inovance.platform
    /// @param annotation annotation
    /// @param <T>        Class type
    /// @return a set contains Class object
    <T> Set<Class<T>> getTypesAnnotatedWith(
            final String pkg, Class<? extends Annotation> annotation,
            final boolean deepScan);
}
