package io.polyaxis.api.utils.reflect.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/// Copy from https://github.com/spring-projects/spring-framework.git, with less modifications
/// Interface for a resource descriptor that abstracts from the actual
/// type of underlying resource, such as a file or class path resource.
/// 
/// An InputStream can be opened for every resource if it exists in
/// physical form, but a URL or File handle can just be returned for
/// certain resources. The actual behavior is implementation-specific.
///
/// @author Juergen Hoeller
///
/// @see #getInputStream()
/// @see #getURL()
/// @see #getURI()
/// @see #getFile()
/// @see WritableResource
/// @see ContextResource
/// @see UrlResource
/// @see FileUrlResource
/// @see FileSystemResource
/// @see ClassPathResource
/// @see ByteArrayResource
/// @see InputStreamResource
/// @since 1.0
public interface Resource extends InputStreamSource {

    /// Determine whether this resource actually exists in physical form.
    ///
    /// This method performs a definitive existence check, whereas the
    /// existence of a `Resource` handle only guarantees a valid
    /// descriptor handle.
    boolean exists();

    /// Indicate whether non-empty contents of this resource can be read via
    /// [#getInputStream()].
    ///
    /// Will be `true` for typical resource descriptors that exist
    /// since it strictly implies [#exists()] semantics as of 5.1.
    /// Note that actual content reading may still fail when attempted.
    /// However, a value of `false` is a definitive indication
    /// that the resource content cannot be read.
    ///
    /// @see #getInputStream()
    /// @see #exists()
    default boolean isReadable() {
        return exists();
    }

    /// Indicate whether this resource represents a handle with an open stream.
    /// If `true`, the InputStream cannot be read multiple times,
    /// and must be read and closed to avoid resource leaks.
    ///
    /// Will be `false` for typical resource descriptors.
    default boolean isOpen() {
        return false;
    }

    /// Determine whether this resource represents a file in a file system.
    /// A value of `true` strongly suggests (but does not guarantee)
    /// that a [#getFile()] call will succeed.
    ///
    /// This is conservatively `false` by default.
    ///
    /// @see #getFile()
    /// @since 5.0
    default boolean isFile() {
        return false;
    }

    /// Return a URL handle for this resource.
    ///
    /// @throws IOException if the resource cannot be resolved as URL,
    ///                     i.e. if the resource is not available as descriptor
    URL getURL() throws IOException;

    /// Return a URI handle for this resource.
    ///
    /// @throws IOException if the resource cannot be resolved as URI,
    ///                     i.e. if the resource is not available as descriptor
    /// @since 2.5
    URI getURI() throws IOException;

    /// Return a File handle for this resource.
    ///
    /// @throws java.io.FileNotFoundException if the resource cannot be resolved as
    ///                                       absolute file path, i.e. if the resource is not available in a file system
    /// @throws IOException                   in case of general resolution/reading failures
    /// @see #getInputStream()
    File getFile() throws IOException;

    /// Return a [ReadableByteChannel].
    ///
    /// It is expected that each call creates a _fresh_ channel.
    ///
    /// The default implementation returns [#newChannel(InputStream)]
    /// with the result of [#getInputStream()].
    ///
    /// @return the byte channel for the underlying resource (must not be `null`)
    /// @throws java.io.FileNotFoundException if the underlying resource doesn't exist
    /// @throws IOException                   if the content channel could not be opened
    /// @see #getInputStream()
    /// @since 5.0
    default ReadableByteChannel readableChannel() throws IOException {
        return Channels.newChannel(getInputStream());
    }

    /// Determine the content length for this resource.
    ///
    /// @throws IOException if the resource cannot be resolved
    ///                     (in the file system or as some other known physical resource type)
    long contentLength() throws IOException;

    /// Determine the last-modified timestamp for this resource.
    ///
    /// @throws IOException if the resource cannot be resolved
    ///                     (in the file system or as some other known physical resource type)
    long lastModified() throws IOException;

    /// Create a resource relative to this resource.
    ///
    /// @param relativePath the relative path (relative to this resource)
    /// @return the resource handle for the relative resource
    /// @throws IOException if the relative resource cannot be determined
    Resource createRelative(String relativePath) throws IOException;

    /// Determine a filename for this resource, i.e. typically the last
    /// part of the path: for example, "myfile.txt".
    ///
    /// Returns `null` if this type of resource does not
    /// have a filename.
    String getFilename();

    /// Return a description for this resource,
    /// to be used for error output when working with the resource.
    ///
    /// Implementations are also encouraged to return this value
    /// from their `toString` method.
    ///
    /// @see Object#toString()
    String getDescription();
}
