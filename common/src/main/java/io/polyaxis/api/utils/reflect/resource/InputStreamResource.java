package io.polyaxis.api.utils.reflect.resource;

import jakarta.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/// Copy from [spring-framework](https://github.com/spring-projects/spring-framework.git),
/// with fewer modifications [Resource] implementation for a given [InputStream].
///
/// Should only be used if no other specific `Resource` implementation
/// is applicable. In particular, prefer [ByteArrayResource] or any of the
/// file-based `Resource` implementations where possible.
///
/// In contrast to other `Resource` implementations, this is a descriptor
/// for an _already opened_ resource - therefore returning `true` from
/// [#isOpen()]. Do not use an `InputStreamResource` if you need to
/// keep the resource descriptor somewhere, or if you need to read from a stream
/// multiple times.
///
/// @author Juergen Hoeller
/// @author Sam Brannen
///
/// @see ByteArrayResource
/// @see ClassPathResource
/// @see FileSystemResource
/// @see UrlResource
///
/// @since 28.12.2003
public class InputStreamResource extends AbstractResource {

    private final InputStreamSource inputStreamSource;

    private final String description;

    private final Object equality;

    private boolean read = false;

    /// Create a new InputStreamResource with a lazy `InputStream`
    /// for single use.
    ///
    /// @param inputStreamSource the InputStream to use
    /// @since 6.1.7
    public InputStreamResource(InputStreamSource inputStreamSource) {
        this(inputStreamSource, "resource loaded through InputStream");
    }

    /// Create a new InputStreamResource with a lazy `InputStream`
    /// for single use.
    ///
    /// @param inputStreamSource the InputStream to use
    /// @param description where the InputStream comes from
    public InputStreamResource(InputStreamSource inputStreamSource, @Nullable String description) {
        Objects.requireNonNull(inputStreamSource, "InputStreamSource must not be null");
        this.inputStreamSource = inputStreamSource;
        this.description = (description != null ? description : "");
        this.equality = inputStreamSource;
    }

    /// Create a new `InputStreamResource` for an existing `InputStream`.
    ///
    /// Consider retrieving the InputStream on demand if possible, reducing its
    /// lifetime and reliably opening it and closing it through regular
    /// [#getInputStream()] usage.
    ///
    /// @param inputStream the InputStream to use
    /// @see #InputStreamResource(InputStreamSource)
    public InputStreamResource(InputStream inputStream) {
        this(inputStream, "resource loaded through InputStream");
    }

    /// Create a new `InputStreamResource` for an existing `InputStream`.
    ///
    /// @param inputStream the InputStream to use
    /// @param description where the InputStream comes from
    /// @see #InputStreamResource(InputStreamSource, String)
    public InputStreamResource(InputStream inputStream, @Nullable String description) {
        Objects.requireNonNull(inputStream, "InputStream must not be null");
        this.inputStreamSource = () -> inputStream;
        this.description = (description != null ? description : "");
        this.equality = inputStream;
    }

    /// This implementation always returns `true`.
    @Override
    public boolean exists() {
        return true;
    }

    /// This implementation always returns `true`.
    @Override
    public boolean isOpen() {
        return true;
    }

    /// This implementation throws IllegalStateException if attempting to
    /// read the underlying stream multiple times.
    @Override
    public InputStream getInputStream() throws IOException, IllegalStateException {
        if (this.read) {
            throw new IllegalStateException("InputStream has already been read (possibly for early content length " +
                    "determination) - do not use InputStreamResource if a stream needs to be read multiple times");
        }
        this.read = true;
        return this.inputStreamSource.getInputStream();
    }

    /// This implementation returns a description that includes the passed-in
    /// description, if any.
    @Override
    public String getDescription() {
        return "InputStream resource [" + this.description + "]";
    }


    /// This implementation compares the underlying InputStream.
    @Override
    public boolean equals(Object other) {
        return (this == other
                || (other instanceof InputStreamResource that
                && this.equality.equals(that.equality)));
    }

    /// This implementation returns the hash code of the underlying InputStream.
    @Override
    public int hashCode() {
        return this.inputStreamSource.hashCode();
    }
}
