package io.polyaxis.api.utils.reflect.resource;

import java.io.IOException;

/// Copy from https://github.com/spring-projects/spring-framework.git, with fewer modifications
/// Strategy interface for resolving a location pattern (for example,
/// an Ant-style path pattern) into [Resource] objects.
///
/// This is an extension to the [ResourceLoader]
/// interface. A passed-in `ResourceLoader` (for example, an
/// can be checked whether it implements this extended interface too.
///
/// [PathMatchingResourcePatternResolver] is a standalone implementation
/// that is usable outside an `ApplicationContext`, also used by
/// properties.
///
/// Can be used with any sort of location pattern (e.g. "/WEB-INF/*-context.xml"):
/// Input patterns have to match the strategy implementation. This interface just
/// specifies the conversion method rather than a specific pattern format.
///
/// This interface also suggests a new resource prefix "classpath*:" for all
/// matching resources from the class path. Note that the resource location is
/// expected to be a path without placeholders in this case (e.g. "/beans.xml");
/// JAR files or different directories in the class path can contain multiple files
/// of the same name.
///
/// @author Juergen Hoeller
/// @see Resource
/// @see ResourceLoader
/// @since 1.0.2
public interface ResourcePatternResolver extends ResourceLoader {

    /// Pseudo URL prefix for all matching resources from the class path: "classpath*:"
    ///
    /// This differs from ResourceLoader's classpath URL prefix in that it
    /// retrieves all matching resources for a given name (e.g. "/beans.xml"),
    /// for example in the root of all deployed JAR files.
    ///
    /// @see ResourceLoader#CLASSPATH_URL_PREFIX
    String CLASSPATH_ALL_URL_PREFIX = "classpath*:";

    /// Resolve the given location pattern into `Resource` objects.
    ///
    /// Overlapping resource entries that point to the same physical
    /// resource should be avoided, as far as possible. The result should
    /// have set semantics.
    ///
    /// @param locationPattern the location pattern to resolve
    /// @return the corresponding `Resource` objects
    /// @throws IOException in case of I/O errors
    Resource[] getResources(String locationPattern) throws IOException;
}
