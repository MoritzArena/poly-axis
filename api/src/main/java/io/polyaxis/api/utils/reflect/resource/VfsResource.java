package io.polyaxis.api.utils.reflect.resource;

import io.polyaxis.api.utils.reflect.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Objects;

/// Copy from https://github.com/spring-projects/spring-framework.git, with less modifications
/// JBoss VFS based [Resource] implementation.
///
/// As of Spring 4.0, this class supports VFS 3.x on JBoss AS 6+
/// (package `org.jboss.vfs`) and is in particular compatible with
/// JBoss AS 7 and WildFly 8+.
///
/// @author Ales Justin
/// @author Juergen Hoeller
/// @author Costin Leau
/// @author Sam Brannen
/// @since 3.0
public class VfsResource extends AbstractResource {

    private final Object resource;

    /// Create a new `VfsResource` wrapping the given resource handle.
    ///
    /// @param resource a `org.jboss.vfs.VirtualFile` instance
    ///                 (untyped in order to avoid a static dependency on the VFS API)
    public VfsResource(Object resource) {
        Objects.requireNonNull(resource, "VirtualFile must not be null");
        this.resource = resource;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return VfsUtils.getInputStream(this.resource);
    }

    @Override
    public boolean exists() {
        return VfsUtils.exists(this.resource);
    }

    @Override
    public boolean isReadable() {
        return VfsUtils.isReadable(this.resource);
    }

    @Override
    public URL getURL() throws IOException {
        try {
            return VfsUtils.getUrl(this.resource);
        } catch (Exception ex) {
            throw new IOException("Failed to obtain URL for file " + this.resource, ex);
        }
    }

    @Override
    public URI getURI() throws IOException {
        try {
            return VfsUtils.getUri(this.resource);
        } catch (Exception ex) {
            throw new IOException("Failed to obtain URI for " + this.resource, ex);
        }
    }

    @Override
    public File getFile() throws IOException {
        return VfsUtils.getFile(this.resource);
    }

    @Override
    public long contentLength() throws IOException {
        return VfsUtils.getSize(this.resource);
    }

    @Override
    public long lastModified() throws IOException {
        return VfsUtils.getLastModified(this.resource);
    }

    @Override
    public Resource createRelative(String relativePath) throws IOException {
        if (!relativePath.startsWith(".") && relativePath.contains("/")) {
            try {
                return new VfsResource(VfsUtils.getChild(this.resource, relativePath));
            } catch (IOException ex) {
                // fall back to getRelative
            }
        }
        return new VfsResource(VfsUtils.getRelative(ResourceUtils.toRelativeURL(this.getURL(), relativePath)));
    }

    @Override
    public String getFilename() {
        return VfsUtils.getName(this.resource);
    }

    @Override
    public String getDescription() {
        return "VFS resource [" + this.resource + "]";
    }

    @Override
    public boolean equals(Object other) {
        return (this == other || (other instanceof VfsResource
                && this.resource.equals(((VfsResource) other).resource)));
    }

    @Override
    public int hashCode() {
        return this.resource.hashCode();
    }
}
