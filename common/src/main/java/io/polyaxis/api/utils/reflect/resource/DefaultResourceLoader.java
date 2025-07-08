package io.polyaxis.api.utils.reflect.resource;

import io.polyaxis.api.utils.misc.ClassUtils;
import io.polyaxis.api.utils.misc.StringUtils;
import io.polyaxis.api.utils.reflect.util.ResourceUtils;
import jakarta.annotation.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/// Default implementation of the [ResourceLoader] interface.
///
/// Used by `ResourceEditor`, and serves as base class for
/// `org.springframework.context.support.AbstractApplicationContext`.
/// Can also be used standalone.
///
/// Will return a [UrlResource] if the location value is a URL,
/// and a [ClassPathResource] if it is a non-URL path or a
/// "classpath:" pseudo-URL.
///
/// @author Juergen Hoeller
/// @since 10.03.2004
public class DefaultResourceLoader implements ResourceLoader {

    @Nullable
    private ClassLoader classLoader;

    private final Set<ProtocolResolver> protocolResolvers = new LinkedHashSet<>(4);

    private final Map<Class<?>, Map<Resource, ?>> resourceCaches = new ConcurrentHashMap<>(4);

    /// Create a new DefaultResourceLoader.
    ///
    /// ClassLoader access will happen using the thread context class loader
    /// at the time of actual resource access (since 5.3). For more control, pass
    /// a specific ClassLoader to [#DefaultResourceLoader(ClassLoader)].
    ///
    /// @see Thread#getContextClassLoader()
    public DefaultResourceLoader() {
    }

    /// Create a new DefaultResourceLoader.
    ///
    /// @param classLoader the ClassLoader to load class path resources with, or `null`
    ///                    for using the thread context class loader at the time of actual resource access
    public DefaultResourceLoader(@Nullable ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /// Specify the ClassLoader to load class path resources with, or `null`
    /// for using the thread context class loader at the time of actual resource access.
    ///
    /// The default is that ClassLoader access will happen using the thread context
    /// class loader at the time of actual resource access (since 5.3).
    public void setClassLoader(@Nullable ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /// Return the ClassLoader to load class path resources with.
    ///
    /// Will get passed to ClassPathResource's constructor for all
    /// ClassPathResource objects created by this resource loader.
    ///
    /// @see ClassPathResource
    @Override
    @Nullable
    public ClassLoader getClassLoader() {
        return (this.classLoader != null ? this.classLoader : ClassUtils.getDefaultClassLoader());
    }

    /// Register the given resolver with this resource loader, allowing for
    /// additional protocols to be handled.
    ///
    /// Any such resolver will be invoked ahead of this loader's standard
    /// resolution rules. It may therefore also override any default rules.
    ///
    /// @see #getProtocolResolvers()
    /// @since 4.3
    public void addProtocolResolver(ProtocolResolver resolver) {
        Objects.requireNonNull(resolver, "ProtocolResolver must not be null");
        this.protocolResolvers.add(resolver);
    }

    /// Return the collection of currently registered protocol resolvers,
    /// allowing for introspection as well as modification.
    ///
    /// @since 4.3
    public Collection<ProtocolResolver> getProtocolResolvers() {
        return this.protocolResolvers;
    }

    /// Obtain a cache for the given value type, keyed by [Resource].
    ///
    /// @param valueType the value type, e.g. an ASM `MetadataReader`
    /// @return the cache [Map], shared at the `ResourceLoader` level
    /// @since 5.0
    @SuppressWarnings("unchecked")
    public <T> Map<Resource, T> getResourceCache(Class<T> valueType) {
        return (Map<Resource, T>) this.resourceCaches.computeIfAbsent(valueType, key -> new ConcurrentHashMap<>());
    }

    /// Clear all resource caches in this resource loader.
    ///
    /// @see #getResourceCache
    /// @since 5.0
    public void clearResourceCaches() {
        this.resourceCaches.clear();
    }

    @Override
    public Resource getResource(String location) {
        Objects.requireNonNull(location, "Location must not be null");

        for (ProtocolResolver protocolResolver : getProtocolResolvers()) {
            Resource resource = protocolResolver.resolve(location, this);
            if (resource != null) {
                return resource;
            }
        }

        if (location.startsWith("/")) {
            return getResourceByPath(location);
        } else if (location.startsWith(CLASSPATH_URL_PREFIX)) {
            return new ClassPathResource(location.substring(CLASSPATH_URL_PREFIX.length()), getClassLoader());
        } else {
            try {
                // Try to parse the location as a URL...
                URL url = ResourceUtils.toURL(location);
                return (ResourceUtils.isFileURL(url) ? new FileUrlResource(url) : new UrlResource(url));
            } catch (MalformedURLException ex) {
                // No URL -> resolve as resource path.
                return getResourceByPath(location);
            }
        }
    }

    /// Return a Resource handle for the resource at the given path.
    ///
    /// The default implementation supports class path locations. This should
    /// be appropriate for standalone implementations but can be overridden,
    /// e.g. for implementations targeted at a Servlet container.
    ///
    /// @param path the path to the resource
    /// @return the corresponding Resource handle
    /// @see ClassPathResource
    protected Resource getResourceByPath(String path) {
        return new ClassPathContextResource(path, getClassLoader());
    }

    /// ClassPathResource that explicitly expresses a context-relative path
    /// through implementing the ContextResource interface.
    protected static class ClassPathContextResource extends ClassPathResource implements ContextResource {

        public ClassPathContextResource(String path, ClassLoader classLoader) {
            super(path, classLoader);
        }

        @Override
        public String getPathWithinContext() {
            return getPath();
        }

        @Override
        public Resource createRelative(String relativePath) {
            String pathToUse = StringUtils.applyRelativePath(getPath(), relativePath);
            return new ClassPathContextResource(pathToUse, getClassLoader());
        }
    }
}
