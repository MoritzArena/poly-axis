package io.polyaxis.api.utils.reflect.resource;

/// Extended interface for a resource that is loaded from an enclosing
/// 'context', for example, from a `ServletContext` but also
/// from plain classpath paths or relative file system paths (specified
/// without an explicit prefix, hence applying relative to the local
/// [ResourceLoader]'s context).
///
/// @author Juergen Hoeller
/// @since 2.5
public interface ContextResource extends Resource {

    /// Return the path within the enclosing 'context'.
    ///
    /// This is typically path relative to a context-specific root directory,
    /// e.g. a ServletContext root or a PortletContext root.
    String getPathWithinContext();
}
