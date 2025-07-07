package io.polyaxis.api.utils.reflect.resource;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.URL;

/// Copy from https://github.com/spring-projects/spring-framework.git, with fewer modifications
/// Artificial class used for accessing the [VfsUtils] methods
/// without exposing them to the entire world.
///
/// @author Costin Leau
/// @since 3.0.3
abstract class VfsPatternUtils extends VfsUtils {

    static Object getVisitorAttributes() {
        return doGetVisitorAttributes();
    }

    static String getPath(Object resource) {
        String path = doGetPath(resource);
        return (path != null ? path : "");
    }

    static Object findRoot(URL url) throws IOException {
        return getRoot(url);
    }

    static void visit(Object resource, InvocationHandler visitor) throws IOException {
        Object visitorProxy = Proxy.newProxyInstance(
                VIRTUAL_FILE_VISITOR_INTERFACE.getClassLoader(),
                new Class<?>[]{VIRTUAL_FILE_VISITOR_INTERFACE}, visitor);
        invokeVfsMethod(VIRTUAL_FILE_METHOD_VISIT, resource, visitorProxy);
    }
}
