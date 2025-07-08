package io.polyaxis.api.utils.reflect.resource;

import io.polyaxis.api.utils.reflect.util.ResourceUtils;

/// Copy from https://github.com/spring-projects/spring-framework.git, with fewer modifications
/// Strategy interface for loading resources (e.g., class path or file system
/// resources).
/// 
/// [DefaultResourceLoader] is a standalone implementation
/// 
/// Bean properties of type `Resource` and `Resource[]` can be populated
/// from Strings when running in an ApplicationContext, using the particular
/// context's resource loading strategy.
///
/// @author Juergen Hoeller
/// @see Resource
/// @see ResourcePatternResolver
/// @since 10.03.2004
public interface ResourceLoader {

    /// Pseudo URL prefix for loading from the class path: "classpath:".
    String CLASSPATH_URL_PREFIX = ResourceUtils.CLASSPATH_URL_PREFIX;

    /// Return a `Resource` handle for the specified resource location.
    ///
    /// The handle should always be a reusable resource descriptor,
    /// allowing for multiple [#getInputStream()] calls.
    ///
    ///   - Must support fully qualified URLs, e.g. "file:C:/test.dat".
    ///   - Must support classpath pseudo-URLs, e.g. "classpath:test.dat".
    ///   - Should support relative file paths, e.g. "WEB-INF/test.dat".
    ///     (This will be implementation-specific, typically provided by an
    ///     ApplicationContext implementation.)
    ///
    ///
    /// Note that a `Resource` handle does not imply an existing resource;
    /// you need to invoke [#exists] to check for existence.
    ///
    /// @param location the resource location
    /// @return a corresponding `Resource` handle (never `null`)
    /// @see #CLASSPATH_URL_PREFIX
    /// @see Resource#exists()
    /// @see Resource#getInputStream()
    Resource getResource(String location);

    /// Expose the [ClassLoader] used by this `ResourceLoader`.
    ///
    /// Clients which need to access the `ClassLoader` directly can do so
    /// in a uniform manner with the `ResourceLoader`, rather than relying
    /// on the thread context `ClassLoader`.
    ///
    /// @return the `ClassLoader`
    /// (only `null` if even the system `ClassLoader` isn't accessible)
    ClassLoader getClassLoader();
}
