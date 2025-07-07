package io.polyaxis.api.utils.reflect.asm;

import io.polyaxis.api.utils.misc.ClassUtils;
import io.polyaxis.api.utils.misc.StringUtils;
import io.polyaxis.api.utils.reflect.resource.PathMatchingResourcePatternResolver;
import io.polyaxis.api.utils.reflect.resource.Resource;
import io.polyaxis.api.utils.reflect.resource.ResourcePatternResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/// Scan all appropriate Class object through the package name.
///
/// @author github.com/MoritzArena
/// @date 2025/07/05
/// @since 1.0
public class DefaultPackageScan implements PackageScan {

    protected static final Logger LOGGER = LoggerFactory.getLogger(DefaultPackageScan.class);

    private static final String EXCLUDED_PACKAGE = "packagescan";

    private final PathMatchingResourcePatternResolver resourcePatternResolver;

    private final ClassLoader classLoader;

    public DefaultPackageScan() {
        classLoader = null;
        this.resourcePatternResolver = new PathMatchingResourcePatternResolver();
    }

    public DefaultPackageScan(ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.resourcePatternResolver = new PathMatchingResourcePatternResolver(classLoader);
    }

    /// Scan all appropriate Class object through the package name and Class object.
    ///
    /// @param pkg          package name, exp, com.inovance.platform
    /// @param requestClass super class
    /// @param <T>          Class type
    /// @return a set contains Class
    @Override
    public <T> Set<Class<T>> getSubTypesOf(
            final String pkg, Class<T> requestClass
    ) {
        Set<Class<T>> set = new HashSet<>(16);
        String packageSearchPath =
                ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(pkg) + '/'
                        + "**/*.class";
        try {
            Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
            for (Resource resource : resources) {
                Class<?> scanClass = getClassByResource(resource);
                if (requestClass.isAssignableFrom(scanClass)) {
                    set.add((Class<T>) scanClass);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.error("scan path: {} failed", packageSearchPath, e);
        }
        return set;
    }

    /// Scan first level appropriate Class object through the package name and annotation.
    ///
    /// @param pkg        package name, exp, com.inovance.platform
    /// @param annotation annotation
    /// @param <T>        Class type
    /// @return a set contains Class object
    @Override
    public <T> Set<Class<T>> getTypesAnnotatedWith(
            final String pkg, Class<? extends Annotation> annotation
    ) {
        return getTypesAnnotatedWith(pkg, annotation, true);
    }

    /// Scan all appropriate Class object through the package name and annotation.
    ///
    /// @param pkg        package name, exp, com.inovance.platform
    /// @param annotation annotation
    /// @param <T>        Class type
    /// @return a set contains Class object
    @Override
    public <T> Set<Class<T>> getTypesAnnotatedWith(
            final String pkg, Class<? extends Annotation> annotation,
            final boolean deepScan
    ) {
        Set<Class<T>> set = new HashSet<>(16);
        final String deepPkg = deepScan ? "/**/*.class" : "/*.class";
        final String packageSearchPath =
                ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                        + ClassUtils.convertClassNameToResourcePath(pkg) + deepPkg;
        try {
            Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
            for (Resource resource : resources) {
                if (StringUtils.contains(resource.getURL().toString(), EXCLUDED_PACKAGE)) {
                    continue;
                }
                Class<?> scanClass = getClassByResource(resource);
                if (scanClass.isAnnotationPresent(annotation)) {
                    set.add((Class<T>) scanClass);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.error("scan path: {} failed", packageSearchPath, e);
        }
        return set;
    }

    private Class<?> getClassByResource(Resource resource) throws IOException, ClassNotFoundException {
        String className = getClassReader(resource).getClassName();
        String classNameInJava = ClassUtils.resourcePathToConvertClassName(className);
        try {
            if (this.classLoader != null) {
                return Class.forName(classNameInJava, true, this.classLoader);
            } else {
                return Class.forName(classNameInJava);
            }
        } catch (Exception e) {
            throw new Error("class not found: " + className, e);
        }
    }

    private static ClassReader getClassReader(Resource resource) throws IOException {
        try (InputStream is = resource.getInputStream()) {
            try {
                return new ClassReader(is);
            } catch (IllegalArgumentException ex) {
                throw new IOException("ASM ClassReader failed to parse class file - "
                        + "probably due to a new Java class file version that isn't supported yet: " + resource, ex);
            }
        }
    }
}
