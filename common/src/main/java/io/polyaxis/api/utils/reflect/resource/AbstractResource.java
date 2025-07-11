package io.polyaxis.api.utils.reflect.resource;

import io.polyaxis.api.utils.reflect.util.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/// Convenience base class for {@link Resource} implementations,
/// pre-implementing typical behavior.
///
/// The "exists" method will check whether a File or InputStream can
/// be opened; "isOpen" will always return false; "getURL" and "getFile"
/// throw an exception; and "toString" will return the description.
///
/// @author Juergen Hoeller
/// @author Sam Brannen
/// @since 28.12.2003
public abstract class AbstractResource implements Resource {

    /// This implementation checks whether a File can be opened,
    /// falling back to whether an InputStream can be opened.
    /// This will cover both directories and content resources.
    @Override
    public boolean exists() {
        // Try file existence: can we find the file in the file system?
        if (isFile()) {
            try {
                return getFile().exists();
            } catch (IOException ex) {
                Logger logger = LoggerFactory.getLogger(getClass());
                if (logger.isDebugEnabled()) {
                    logger.debug("Could not retrieve File for existence check of " + getDescription(), ex);
                }
            }
        }
        // Fall back to stream existence: can we open the stream?
        try {
            getInputStream().close();
            return true;
        } catch (Throwable ex) {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (logger.isDebugEnabled()) {
                logger.debug("Could not retrieve InputStream for existence check of " + getDescription(), ex);
            }
            return false;
        }
    }

    /// This implementation always returns `true` for a resource
    /// that [exists][#exists()] (revised as of 5.1).
    @Override
    public boolean isReadable() {
        return exists();
    }

    /// This implementation always returns `false`.
    @Override
    public boolean isOpen() {
        return false;
    }

    /// This implementation always returns `false`.
    @Override
    public boolean isFile() {
        return false;
    }

    /// This implementation throws a FileNotFoundException, assuming
    /// that the resource cannot be resolved to a URL.
    @Override
    public URL getURL() throws IOException {
        throw new FileNotFoundException(getDescription() + " cannot be resolved to URL");
    }

    /// This implementation builds a URI based on the URL returned
    /// by [#getURL()].
    @Override
    public URI getURI() throws IOException {
        URL url = getURL();
        try {
            return ResourceUtils.toURI(url);
        } catch (URISyntaxException ex) {
            throw new IOException("Invalid URI [" + url + "]", ex);
        }
    }

    /// This implementation throws a FileNotFoundException, assuming
    /// that the resource cannot be resolved to an absolute file path.
    @Override
    public File getFile() throws IOException {
        throw new FileNotFoundException(getDescription() + " cannot be resolved to absolute file path");
    }

    /// This implementation returns [Channels#newChannel(InputStream)]
    /// with the result of [#getInputStreamSource()].
    /// This is the same as in [Resource]'s corresponding default method
    /// but mirrored here for efficient JVM-level dispatching in a class hierarchy.
    @Override
    public ReadableByteChannel readableChannel() throws IOException {
        return Channels.newChannel(getInputStream());
    }

    /// This method reads the entire InputStream to determine the content length.
    /// For a custom subclass of `InputStreamResource`, we strongly
    /// recommend overriding this method with a more optimal implementation, e.g.
    /// checking File length, or possibly simply returning -1 if the stream can
    /// only be read once.
    ///
    /// @see #getInputStream()
    @Override
    public long contentLength() throws IOException {
        InputStream is = getInputStream();
        try {
            long size = 0;
            byte[] buf = new byte[256];
            int read;
            while ((read = is.read(buf)) != -1) {
                size += read;
            }
            return size;
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                Logger logger = LoggerFactory.getLogger(getClass());
                if (logger.isDebugEnabled()) {
                    logger.debug("Could not close content-length InputStream for " + getDescription(), ex);
                }
            }
        }
    }

    /// This implementation checks the timestamp of the underlying File,
    /// if available.
    ///
    /// @see #getFileForLastModifiedCheck()
    @Override
    public long lastModified() throws IOException {
        File fileToCheck = getFileForLastModifiedCheck();
        long lastModified = fileToCheck.lastModified();
        if (lastModified == 0L && !fileToCheck.exists()) {
            throw new FileNotFoundException(getDescription()
                    + " cannot be resolved in the file system for checking its last-modified timestamp");
        }
        return lastModified;
    }

    /// Determine the File to use for timestamp checking.
    /// The default implementation delegates to [#getFile()].
    ///
    /// @return the File to use for timestamp checking \(never `null`\)
    /// @throws FileNotFoundException if the resource cannot be resolved as
    ///                               an absolute file path, i.e. is not available in a file system
    /// @throws IOException           in case of general resolution/reading failures
    protected File getFileForLastModifiedCheck() throws IOException {
        return getFile();
    }

    /// This implementation throws a FileNotFoundException, assuming
    /// that relative resources cannot be created for this resource.
    @Override
    public Resource createRelative(String relativePath) throws IOException {
        throw new FileNotFoundException("Cannot create a relative resource for " + getDescription());
    }

    /// This implementation always returns `null`,
    /// assuming that this resource type does not have a filename.
    @Override

    public String getFilename() {
        return null;
    }


    /// This implementation compares description strings.
    ///
    /// @see #getDescription()
    @Override
    public boolean equals(Object other) {
        return (this == other || (other instanceof Resource
                && ((Resource) other).getDescription().equals(getDescription())));
    }

    /// This implementation returns the description's hash code.
    ///
    /// @see #getDescription()
    @Override
    public int hashCode() {
        return getDescription().hashCode();
    }

    /// This implementation returns the description of this resource.
    ///
    /// @see #getDescription()
    @Override
    public String toString() {
        return getDescription();
    }
}
