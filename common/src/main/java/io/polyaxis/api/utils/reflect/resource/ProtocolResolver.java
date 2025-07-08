package io.polyaxis.api.utils.reflect.resource;

/// Copy from https://github.com/spring-projects/spring-framework.git, with fewer modifications
/// A resolution strategy for protocol-specific resource handles.
///
/// Used as an SPI for [DefaultResourceLoader], allowing for
/// custom protocols to be handled without subclassing the loader
/// implementation (or application context implementation).
///
/// @author Juergen Hoeller
///
/// @see DefaultResourceLoader#addProtocolResolver
///
/// @since 4.3
@FunctionalInterface
public interface ProtocolResolver {

    /// Resolve the given location against the given resource loader
    /// if this implementation's protocol matches.
    ///
    /// @param location       the user-specified resource location
    /// @param resourceLoader the associated resource loader
    /// @return a corresponding `Resource` handle if the given location
    /// matches this resolver's protocol, or `null` otherwise
    Resource resolve(String location, ResourceLoader resourceLoader);
}
