package io.polyaxis.api.utils.reflect.resource;

import io.polyaxis.api.utils.reflect.util.ResourceUtils;
import jakarta.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

/// Copy from [spring-framework](https://github.com/spring-projects/spring-framework.git),
/// with fewer modifications Subclass of [UrlResource] which assumes file resolution, to
/// the degree of implementing the [WritableResource] interface for it. This resource
/// variant also caches resolved [File] handles from [#getFile()].
///
/// This is the class resolved by [DefaultResourceLoader] for a "file:..."
/// URL location, allowing a downcast to [WritableResource] for it.
///
/// Alternatively, for direct construction from a [File] handle
/// or NIO [java.nio.file.Path], consider using [FileSystemResource].
///
/// @author Juergen Hoeller
/// @since 5.0.2
public class FileUrlResource extends UrlResource implements WritableResource {

    @Nullable
    private volatile File file;

    /// Create a new `FileUrlResource` based on the given URL object.
    ///
    /// Note that this does not enforce "file" as URL protocol. If a protocol
    /// is known to be resolvable to a file, it is acceptable for this purpose.
    ///
    /// @param url a URL
    /// @see ResourceUtils#isFileURL(URL)
    /// @see #getFile()
    public FileUrlResource(URL url) {
        super(url);
    }

    /// Create a new `FileUrlResource` based on the given file location,
    /// using the URL protocol "file".
    ///
    /// The given parts will automatically get encoded if necessary.
    ///
    /// @param location the location (i.e. the file path within that protocol)
    /// @throws MalformedURLException if the given URL specification is not valid
    /// @see UrlResource#UrlResource(String, String)
    /// @see ResourceUtils#URL_PROTOCOL_FILE
    public FileUrlResource(String location) throws MalformedURLException {
        super(ResourceUtils.URL_PROTOCOL_FILE, location);
    }

    @Override
    public File getFile() throws IOException {
        File file = this.file;
        if (file != null) {
            return file;
        }
        file = super.getFile();
        this.file = file;
        return file;
    }

    @Override
    public boolean isWritable() {
        try {
            File file = getFile();
            return (file.canWrite() && !file.isDirectory());
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return Files.newOutputStream(getFile().toPath());
    }

    @Override
    public WritableByteChannel writableChannel() throws IOException {
        return FileChannel.open(getFile().toPath(), StandardOpenOption.WRITE);
    }

    @Override
    public Resource createRelative(String relativePath) throws MalformedURLException {
        return new FileUrlResource(createRelativeURL(relativePath));
    }
}
