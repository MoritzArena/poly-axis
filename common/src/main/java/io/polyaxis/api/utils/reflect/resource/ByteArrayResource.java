package io.polyaxis.api.utils.reflect.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;

/// [Resource] implementation for a given byte array.
///
/// Creates a [ByteArrayInputStream] for the given byte array.
///
/// Useful for loading content from any given byte array,
/// without having to resort to a single-use [InputStreamResource].
/// Particularly useful for creating mail attachments from local content,
/// where JavaMail needs to be able to read the stream multiple times.
///
/// @author Juergen Hoeller
/// @author Sam Brannen
/// @since 1.2.3
/// @see java.io.ByteArrayInputStream
/// @see InputStreamResource
public class ByteArrayResource extends AbstractResource {

    private final byte[] byteArray;

    private final String description;

    /// Create a new `ByteArrayResource`.
    ///
    /// @param byteArray the byte array to wrap
    public ByteArrayResource(byte[] byteArray) {
        this(byteArray, "resource loaded from byte array");
    }

    /// Create a new `ByteArrayResource` with a description.
    ///
    /// @param byteArray   the byte array to wrap
    /// @param description where the byte array comes from
    public ByteArrayResource(byte[] byteArray, String description) {
        Objects.requireNonNull(byteArray, "Byte array must not be null");
        this.byteArray = byteArray;
        this.description = (description != null ? description : "");
    }

    /// Return the underlying byte array.
    public final byte[] getByteArray() {
        return this.byteArray;
    }

    /// This implementation always returns `true`.
    @Override
    public boolean exists() {
        return true;
    }

    /// This implementation returns the length of the underlying byte array.
    @Override
    public long contentLength() {
        return this.byteArray.length;
    }

    /// This implementation returns a ByteArrayInputStream for the
    /// underlying byte array.
    ///
    /// @see ByteArrayInputStream
    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.byteArray);
    }

    /// This implementation returns a description that includes the passed-in
    /// `description`, if any.
    @Override
    public String getDescription() {
        return "Byte array resource [" + this.description + "]";
    }

    /// This implementation compares the underlying byte array.
    ///
    /// @see Arrays#equals(byte[], byte[])
    @Override
    public boolean equals(Object other) {
        return (this == other
                || (other instanceof ByteArrayResource
                && Arrays.equals(((ByteArrayResource) other).byteArray, this.byteArray)));
    }

    /// This implementation returns the hash code based on the
    /// underlying byte array.
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.byteArray);
    }
}
