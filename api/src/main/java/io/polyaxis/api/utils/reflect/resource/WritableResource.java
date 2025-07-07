package io.polyaxis.api.utils.reflect.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

/// Extended interface for a resource that supports writing to it.
/// Provides an [OutputStream accessor][#getOutputStream()].
///
/// @author Juergen Hoeller
/// @see OutputStream
/// @since 3.1
public interface WritableResource extends Resource {

    /// Indicate whether the contents of this resource can be written
    /// via [#getOutputStream()].
    ///
    /// Will be `true` for typical resource descriptors;
    /// note that actual content writing may still fail when attempted.
    /// However, a value of `false` is a definitive indication
    /// that the resource content cannot be modified.
    ///
    /// @see #getOutputStream()
    /// @see #isReadable()
    default boolean isWritable() {
        return true;
    }

    /// Return an [OutputStream] for the underlying resource,
    /// allowing to (over-)write its content.
    ///
    /// @throws IOException if the stream could not be opened
    /// @see #getInputStream()
    OutputStream getOutputStream() throws IOException;

    /// Return a [WritableByteChannel].
    ///
    /// It is expected that each call creates a _fresh_ channel.
    ///
    /// The default implementation returns [#newChannel(OutputStream)]
    /// with the result of [#getOutputStream()].
    ///
    /// @return the byte channel for the underlying resource (must not be `null`)
    /// @throws java.io.FileNotFoundException if the underlying resource doesn't exist
    /// @throws IOException                   if the content channel could not be opened
    /// @see #getOutputStream()
    /// @since 5.0
    default WritableByteChannel writableChannel() throws IOException {
        return Channels.newChannel(getOutputStream());
    }

}
