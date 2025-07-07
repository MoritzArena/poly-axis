package io.polyaxis.api.utils.reflect.resource;

import java.io.IOException;
import java.io.InputStream;

/// Copy from [spring-framework](https://github.com/spring-projects/spring-framework.git),
/// with fewer modifications Simple interface for objects that are sources for an [InputStream].
///
/// This is the base interface for Spring's more extensive [Resource] interface.
///
/// For single-use streams, [InputStreamResource] can be used for any
/// given `InputStream`. Spring's [ByteArrayResource] or any
/// file-based `Resource` implementation can be used as a concrete
/// instance, allowing one to read the underlying content stream multiple times.
/// This makes this interface useful as an abstract content source for mail
/// attachments, for example.
///
/// @author Juergen Hoeller
///
/// @see InputStream
/// @see Resource
/// @see InputStreamResource
/// @see ByteArrayResource
///
/// @since 20.01.2004
public interface InputStreamSource {

    /// Return an [InputStream] for the content of an underlying resource.
    ///
    /// It is expected that each call creates a _fresh_ stream.
    ///
    /// This requirement is particularly important when you consider an API such
    /// as JavaMail, which needs to be able to read the stream multiple times when
    /// creating mail attachments. For such a use case, it is _required_
    /// that each `getInputStream()` call returns a fresh stream.
    ///
    /// @return the input stream for the underlying resource (must not be `null`)
    /// @throws java.io.FileNotFoundException if the underlying resource does not exist
    /// @throws IOException                   if the content stream could not be opened
    /// @see Resource#isReadable()
    InputStream getInputStream() throws IOException;
}
