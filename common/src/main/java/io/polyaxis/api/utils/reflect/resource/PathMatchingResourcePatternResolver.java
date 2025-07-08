package io.polyaxis.api.utils.reflect.resource;

import io.polyaxis.api.utils.aot.NativeDetector;
import io.polyaxis.api.utils.misc.ClassUtils;
import io.polyaxis.api.utils.misc.StringUtils;
import io.polyaxis.api.utils.reflect.util.PathMatcher;
import io.polyaxis.api.utils.reflect.util.ResourceUtils;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReader;
import java.lang.module.ResolvedModule;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipException;

/// Copy from <a href="https://github.com/spring-projects/spring-framework.git">spring-framework</a>, with fewer modifications
/// A [ResourcePatternResolver] implementation that is able to resolve a
/// specified resource location path into one or more matching Resources.
/// The source path may be a simple path which has a one-to-one mapping to a
/// target [Resource], or alternatively
/// may contain the special "`classpath*:`" prefix and/or
/// internal Ant-style regular expressions (matched using Spring's
/// [AntPathMatcher] utility).
/// Both of the latter are effectively wildcards.
///
/// **No Wildcards:**
///
/// In the simple case, if the specified location path does not start with the
/// `"classpath*:`" prefix, and does not contain a PathMatcher pattern,
/// this resolver will simply return a single resource via a
/// `getResource()` call on the underlying `ResourceLoader`.
/// Examples are real URLs such as "`file:C:/context.xml`", pseudo-URLs
/// such as "`classpath:/context.xml`", and simple unprefixed paths
/// such as "`/WEB-INF/context.xml`". The latter will resolve in a
/// fashion specific to the underlying `ResourceLoader` (e.g.
/// `ServletContextResource` for a `WebApplicationContext`).
///
/// **Ant-style Patterns:**
///
/// When the path location contains an Ant-style pattern, e.g.:
/// <pre class="code">
/// /WEB-INF/*-context.xml
/// com/mycompany/**&#47;applicationContext.xml
/// file:C:/some/path/*-context.xml
/// classpath:com/mycompany/**&#47;applicationContext.xml</pre>
/// the resolver follows a more complex but defined procedure to try to resolve
/// the wildcard. It produces a `Resource` for the path up to the last
/// non-wildcard segment and obtains a `URL` from it. If this URL is
/// not a "`jar:`" URL or container-specific variant (e.g.
/// "`zip:`" in WebLogic, "`wsjar`" in WebSphere", etc.),
/// then a `java.io.File` is obtained from it, and used to resolve the
/// wildcard by walking the filesystem. In the case of a jar URL, the resolver
/// either gets a `java.net.JarURLConnection` from it, or manually parses
/// the jar URL, and then traverses the contents of the jar file, to resolve the
/// wildcards.
///
/// **Implications on portability:**
///
/// If the specified path is already a file URL (either explicitly, or
/// implicitly because the base `ResourceLoader` is a filesystem one,
/// then wildcarding is guaranteed to work in a completely portable fashion.
///
/// If the specified path is a classpath location, then the resolver must
/// obtain the last non-wildcard path segment URL via a
/// `Classloader.getResource()` call. Since this is just a
/// node of the path (not the file at the end) it is actually undefined
/// (in the ClassLoader Javadocs) exactly what sort of a URL is returned in
/// this case. In practice, it is usually a `java.io.File` representing
/// the directory, where the classpath resource resolves to a filesystem
/// location, or a jar URL of some sort, where the classpath resource resolves
/// to a jar location. Still, there is a portability concern on this operation.
///
/// If a jar URL is obtained for the last non-wildcard segment, the resolver
/// must be able to get a `java.net.JarURLConnection` from it, or
/// manually parse the jar URL, to be able to walk the contents of the jar,
/// and resolve the wildcard. This will work in most environments, but will
/// fail in others, and it is strongly recommended that the wildcard
/// resolution of resources coming from jars be thoroughly tested in your
/// specific environment before you rely on it.
///
/// **`classpath*:` Prefix:**
///
/// There is special support for retrieving multiple class path resources with
/// the same name, via the "`classpath*:`" prefix. For example,
/// "`classpath*:META-INF/beans.xml`" will find all "beans.xml"
/// files in the class path, be it in "classes" directories or in JAR files.
/// This is particularly useful for autodetecting config files of the same name
/// at the same location within each jar file. Internally, this happens via a
/// `ClassLoader.getResources()` call, and is completely portable.
///
/// The "classpath*:" prefix can also be combined with a PathMatcher pattern in
/// the rest of the location path, for example "classpath*:META-INF/*-beans.xml".
/// In this case, the resolution strategy is fairly simple: a
/// `ClassLoader.getResources()` call is used on the last non-wildcard
/// path segment to get all the matching resources in the class loader hierarchy,
/// and then off each resource the same PathMatcher resolution strategy described
/// above is used for the wildcard subpath.
///
/// **Other notes:**
///
/// **WARNING:** Note that "`classpath*:`" when combined with
/// Ant-style patterns will only work reliably with at least one root directory
/// before the pattern starts, unless the actual target files reside in the file
/// system. This means that a pattern like "`classpath*:*.xml`" will
/// _not_ retrieve files from the root of jar files but rather only from the
/// root of expanded directories. This originates from a limitation in the JDK's
/// `ClassLoader.getResources()` method which only returns file system
/// locations for a passed-in empty String (indicating potential roots to search).
/// This `ResourcePatternResolver` implementation is trying to mitigate the
/// jar root lookup limitation through [URLClassLoader] introspection and
/// "java.class.path" manifest evaluation; however, without portability guarantees.
///
/// **WARNING:** Ant-style patterns with "classpath:" resources are not
/// guaranteed to find matching resources if the root package to search is available
/// in multiple class path locations. This is because a resource such as
/// <pre class="code">
///     com/mycompany/package1/service-context.xml
/// </pre>
/// may be in only one location, but when a path such as
/// <pre class="code">
///     classpath:com/mycompany/**&#47;service-context.xml
/// </pre>
/// is used to try to resolve it, the resolver will work off the (first) URL
/// returned by `getResource("com/mycompany");`. If this base package node
/// exists in multiple classloader locations, the actual end resource may not be
/// underneath. Therefore, preferably, use "`classpath*:`" with the same
/// Ant-style pattern in such a case, which will search _all_ class path
/// locations that contain the root package.
///
/// @author Juergen Hoeller
/// @author Colin Sampaleanu
/// @author Marius Bogoevici
/// @author Costin Leau
/// @author Phillip Webb
/// @see #CLASSPATH_ALL_URL_PREFIX
///
/// @see AntPathMatcher
/// @see ResourceLoader#getResource(String)
/// @see ClassLoader#getResources(String)
///
/// @since 1.0
public class PathMatchingResourcePatternResolver implements ResourcePatternResolver {

    private static final Resource[] EMPTY_RESOURCE_ARRAY = {};

    private static final Logger LOGGER = LoggerFactory.getLogger(PathMatchingResourcePatternResolver.class);

    /// [Set] of {@linkplain ModuleFinder#ofSystem() system module} names.
    ///
    /// @since 6.0
    /// @see #isNotSystemModule
    private static final Set<String> systemModuleNames = NativeDetector.inNativeImage() ? Collections.emptySet() :
            ModuleFinder.ofSystem().findAll().stream()
                    .map(moduleReference -> moduleReference.descriptor().name())
                    .collect(Collectors.toSet());

    /// [Predicate] that tests whether the supplied [ResolvedModule]
    /// is not a {@linkplain ModuleFinder#ofSystem() system module}.
    ///
    /// @since 6.0
    /// @see #systemModuleNames
    private static final Predicate<ResolvedModule> isNotSystemModule =
            resolvedModule -> !systemModuleNames.contains(resolvedModule.name());

    @Nullable
    private static Method equinoxResolveMethod;

    static {
        try {
            // Detect Equinox OSGi (for example, on WebSphere 6.1)
            Class<?> fileLocatorClass = ClassUtils.forName("org.eclipse.core.runtime.FileLocator",
                    PathMatchingResourcePatternResolver.class.getClassLoader());
            equinoxResolveMethod = fileLocatorClass.getMethod("resolve", URL.class);
            LOGGER.trace("Found Equinox FileLocator for OSGi bundle URL resolution");
        }
        catch (Throwable ex) {
            equinoxResolveMethod = null;
        }
    }

    private final ResourceLoader resourceLoader;

    private PathMatcher pathMatcher = new AntPathMatcher();

    private boolean useCaches = true;

    private final Map<String, Resource[]> rootDirCache = new ConcurrentHashMap<>();

    private final Map<String, NavigableSet<String>> jarEntriesCache = new ConcurrentHashMap<>();

    @Nullable
    private volatile Set<ClassPathManifestEntry> manifestEntriesCache;


    /// Create a `PathMatchingResourcePatternResolver` with a
    /// [DefaultResourceLoader].
    ///
    /// ClassLoader access will happen via the thread context class loader.
    ///
    /// @see DefaultResourceLoader
    public PathMatchingResourcePatternResolver() {
        this.resourceLoader = new DefaultResourceLoader();
    }

    /// Create a `PathMatchingResourcePatternResolver` with the supplied
    /// [ResourceLoader].
    ///
    /// ClassLoader access will happen via the thread context class loader.
    ///
    /// @param resourceLoader the `ResourceLoader` to load root directories
    /// and actual resources with
    public PathMatchingResourcePatternResolver(ResourceLoader resourceLoader) {
        Objects.requireNonNull(resourceLoader, "ResourceLoader must not be null");
        this.resourceLoader = resourceLoader;
    }

    /// Create a `PathMatchingResourcePatternResolver` with a
    /// [DefaultResourceLoader] and the supplied [ClassLoader].
    /// @param classLoader the ClassLoader to load class path resources with,
    /// or `null` for using the thread context class loader
    /// at the time of actual resource access
    ///
    /// @see DefaultResourceLoader
    public PathMatchingResourcePatternResolver(@Nullable ClassLoader classLoader) {
        this.resourceLoader = new DefaultResourceLoader(classLoader);
    }


    /// Return the [ResourceLoader] that this pattern resolver works with.
    public ResourceLoader getResourceLoader() {
        return this.resourceLoader;
    }

    @Override
    @Nullable
    public ClassLoader getClassLoader() {
        return getResourceLoader().getClassLoader();
    }

    /// Set the [PathMatcher] implementation to use for this
    /// resource pattern resolver.
    ///
    /// Default is [AntPathMatcher].
    /// @see AntPathMatcher
    public void setPathMatcher(PathMatcher pathMatcher) {
        Objects.requireNonNull(pathMatcher, "PathMatcher must not be null");
        this.pathMatcher = pathMatcher;
    }

    /// Return the [PathMatcher] that this resource pattern resolver uses.
    public PathMatcher getPathMatcher() {
        return this.pathMatcher;
    }

    /// Specify whether this resolver should use jar caches. Default is `true`.
    ///
    /// Switch this flag to `false` in order to avoid any jar caching, at
    /// the [JarURLConnection] level as well as within this resolver instance.
    ///
    /// Note that [#setDefaultUseCaches] can be turned off
    /// independently. This resolver-level setting is designed to only enforce
    /// `JarURLConnection#setUseCaches(false)` if necessary but otherwise
    /// leaves the JVM-level default in place.
    ///
    /// @since 6.1.19
    /// @see JarURLConnection#setUseCaches
    /// @see #clearCache()
    public void setUseCaches(boolean useCaches) {
        this.useCaches = useCaches;
    }


    @Override
    public Resource getResource(String location) {
        return getResourceLoader().getResource(location);
    }

    @Override
    public Resource[] getResources(String locationPattern) throws IOException {
        Objects.requireNonNull(locationPattern, "Location pattern must not be null");
        if (locationPattern.startsWith(CLASSPATH_ALL_URL_PREFIX)) {
            // a class path resource (multiple resources for same name possible)
            String locationPatternWithoutPrefix = locationPattern.substring(CLASSPATH_ALL_URL_PREFIX.length());
            // Search the module path first.
            Set<Resource> resources = findAllModulePathResources(locationPatternWithoutPrefix);
            // Search the class path next.
            if (getPathMatcher().isPattern(locationPatternWithoutPrefix)) {
                // a class path resource pattern
                Collections.addAll(resources, findPathMatchingResources(locationPattern));
            }
            else {
                // all class path resources with the given name
                Collections.addAll(resources, findAllClassPathResources(locationPatternWithoutPrefix));
            }
            return resources.toArray(EMPTY_RESOURCE_ARRAY);
        }
        else {
            // Generally only look for a pattern after a prefix here,
            // and on Tomcat only after the "*/" separator for its "war:" protocol.
            int prefixEnd = (locationPattern.startsWith("war:") ? locationPattern.indexOf("*/") + 1 :
                    locationPattern.indexOf(':') + 1);
            if (getPathMatcher().isPattern(locationPattern.substring(prefixEnd))) {
                // a file pattern
                return findPathMatchingResources(locationPattern);
            }
            else {
                // a single resource with the given name
                return new Resource[] {getResourceLoader().getResource(locationPattern)};
            }
        }
    }

    /**
     * Clear the local resource cache, removing all cached classpath/jar structures.
     * @since 6.2
     */
    public void clearCache() {
        this.rootDirCache.clear();
        this.jarEntriesCache.clear();
        this.manifestEntriesCache = null;
    }


    /// Find all class location resources with the given location via the ClassLoader.
    ///
    /// Delegates to [#doFindAllClassPathResources(String)].
    ///
    /// @param location the absolute path within the class path
    /// @return the result as Resource array
    /// @throws IOException in case of I/O errors
    /// @see java.lang.ClassLoader#getResources
    /// @see #convertClassLoaderURL
    protected Resource[] findAllClassPathResources(String location) throws IOException {
        String path = stripLeadingSlash(location);
        Set<Resource> result = doFindAllClassPathResources(path);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Resolved class path location [{}] to resources {}", path, result);
        }
        return result.toArray(EMPTY_RESOURCE_ARRAY);
    }

    /// Find all class path resources with the given path via the configured
    /// [ClassLoader][#getClassLoader()].
    ///
    /// Called by [#findAllClassPathResources(String)].
    ///
    /// @param path the absolute path within the class path (never a leading slash)
    /// @return a mutable Set of matching Resource instances
    /// @since 4.1.1
    protected Set<Resource> doFindAllClassPathResources(String path) throws IOException {
        Set<Resource> result = new LinkedHashSet<>(16);
        ClassLoader cl = getClassLoader();
        Enumeration<URL> resourceUrls = (cl != null ? cl.getResources(path) : ClassLoader.getSystemResources(path));
        while (resourceUrls.hasMoreElements()) {
            URL url = resourceUrls.nextElement();
            result.add(convertClassLoaderURL(url));
        }
        if (!StringUtils.hasLength(path)) {
            // The above result is likely to be incomplete, i.e. only containing file system references.
            // We need to have pointers to each of the jar files on the class path as well...
            addAllClassLoaderJarRoots(cl, result);
        }
        return result;
    }

    /// Convert the given URL as returned from the configured
    /// [ClassLoader][#getClassLoader()] into a [Resource], applying
    /// to path lookups without a pattern (see [#findAllClassPathResources]).
    ///
    /// As of 6.0.5, the default implementation creates a [FileSystemResource]
    /// in case of the "file" protocol or a [UrlResource] otherwise, matching
    /// the outcome of pattern-based class path traversal in the same resource layout,
    /// as well as matching the outcome of module path searches.
    ///
    /// @param url a URL as returned from the configured ClassLoader
    /// @return the corresponding Resource object
    /// @see java.lang.ClassLoader#getResources
    /// @see #doFindAllClassPathResources
    /// @see #doFindPathMatchingFileResources
    @SuppressWarnings("deprecation")  // on JDK 20 (deprecated URL constructor)
    protected Resource convertClassLoaderURL(URL url) {
        if (ResourceUtils.URL_PROTOCOL_FILE.equals(url.getProtocol())) {
            try {
                // URI decoding for special characters such as spaces.
                return new FileSystemResource(ResourceUtils.toURI(url).getSchemeSpecificPart());
            }
            catch (URISyntaxException ex) {
                // Fallback for URLs that are not valid URIs (should hardly ever happen).
                return new FileSystemResource(url.getFile());
            }
        }
        else {
            String urlString = url.toString();
            String cleanedPath = StringUtils.cleanPath(urlString);
            if (!cleanedPath.equals(urlString)) {
                // Prefer cleaned URL, aligned with UrlResource#createRelative(String)
                try {
                    // Retain original URL instance, potentially including custom URLStreamHandler.
                    return new UrlResource(new URL(url, cleanedPath));
                }
                catch (MalformedURLException ex) {
                    // Fallback to regular URL construction below...
                }
            }
            // Retain original URL instance, potentially including custom URLStreamHandler.
            return new UrlResource(url);
        }
    }

    /// Search all [URLClassLoader] URLs for jar file references and add each to the
    /// given set of resources in the form of a pointer to the root of the jar file content.
    ///
    /// @param classLoader the ClassLoader to search (including its ancestors)
    /// @param result the set of resources to add jar roots to
    /// @since 4.1.1
    protected void addAllClassLoaderJarRoots(@Nullable ClassLoader classLoader, Set<Resource> result) {
        if (classLoader instanceof URLClassLoader urlClassLoader) {
            try {
                for (URL url : urlClassLoader.getURLs()) {
                    try {
                        UrlResource jarResource = (ResourceUtils.URL_PROTOCOL_JAR.equals(url.getProtocol()) ?
                                new UrlResource(url) :
                                new UrlResource(ResourceUtils.JAR_URL_PREFIX + url + ResourceUtils.JAR_URL_SEPARATOR));
                        if (jarResource.exists()) {
                            result.add(jarResource);
                        }
                    }
                    catch (MalformedURLException ex) {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Cannot search for matching files underneath [{}"
                                    + "] because it cannot be converted to a valid 'jar:' URL: ", url, ex);
                        }
                    }
                }
            }
            catch (Exception ex) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Cannot introspect jar files since ClassLoader [{}"
                            + "] does not support 'getURLs()': ", classLoader, ex);
                }
            }
        }

        if (classLoader == ClassLoader.getSystemClassLoader()) {
            // JAR "Class-Path" manifest header evaluation...
            addClassPathManifestEntries(result);
        }

        if (classLoader != null) {
            try {
                // Hierarchy traversal...
                addAllClassLoaderJarRoots(classLoader.getParent(), result);
            }
            catch (Exception ex) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Cannot introspect jar files in parent ClassLoader since [{}"
                            + "] does not support 'getParent()': ", classLoader, ex);
                }
            }
        }
    }

    /// Determine jar file references from `Class-Path` manifest entries (which
    /// are added to the `java.class.path` JVM system property by the system
    /// class loader) and add each to the given set of resources in the form of
    /// a pointer to the root of the jar file content.
    ///
    /// @param result the set of resources to add jar roots to
    /// @since 4.3
    protected void addClassPathManifestEntries(Set<Resource> result) {
        Set<ClassPathManifestEntry> entries = this.manifestEntriesCache;
        if (entries == null) {
            entries = getClassPathManifestEntries();
            if (this.useCaches) {
                this.manifestEntriesCache = entries;
            }
        }
        for (ClassPathManifestEntry entry : entries) {
            if (!result.contains(entry.resource()) &&
                    (entry.alternative() != null && !result.contains(entry.alternative()))) {
                result.add(entry.resource());
            }
        }
    }

    private Set<ClassPathManifestEntry> getClassPathManifestEntries() {
        Set<ClassPathManifestEntry> manifestEntries = new LinkedHashSet<>();
        Set<File> seen = new HashSet<>();
        try {
            String paths = System.getProperty("java.class.path");
            for (String path : StringUtils.delimitedListToStringArray(paths, File.pathSeparator)) {
                try {
                    File jar = new File(path).getAbsoluteFile();
                    if (jar.isFile() && seen.add(jar)) {
                        manifestEntries.add(ClassPathManifestEntry.of(jar));
                        manifestEntries.addAll(getClassPathManifestEntriesFromJar(jar));
                    }
                }
                catch (MalformedURLException ex) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Cannot search for matching files underneath [{}"
                                + "] because it cannot be converted to a valid 'jar:' URL:", path, ex);
                    }
                }
            }
            return Collections.unmodifiableSet(manifestEntries);
        }
        catch (Exception ex) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Failed to evaluate 'java.class.path' manifest entries: " + ex);
            }
            return Collections.emptySet();
        }
    }

    private Set<ClassPathManifestEntry> getClassPathManifestEntriesFromJar(File jar) throws IOException {
        URL base = jar.toURI().toURL();
        File parent = jar.getAbsoluteFile().getParentFile();
        try (JarFile jarFile = new JarFile(jar)) {
            Manifest manifest = jarFile.getManifest();
            Attributes attributes = (manifest != null ? manifest.getMainAttributes() : null);
            String classPath = (attributes != null ? attributes.getValue(Attributes.Name.CLASS_PATH) : null);
            Set<ClassPathManifestEntry> manifestEntries = new LinkedHashSet<>();
            if (StringUtils.hasLength(classPath)) {
                StringTokenizer tokenizer = new StringTokenizer(classPath);
                while (tokenizer.hasMoreTokens()) {
                    String path = tokenizer.nextToken();
                    if (path.indexOf(':') >= 0 && !"file".equalsIgnoreCase(new URL(base, path).getProtocol())) {
                        // See jdk.internal.loader.URLClassPath.JarLoader.tryResolveFile(URL, String)
                        continue;
                    }
                    File candidate = new File(parent, path);
                    if (candidate.isFile() && candidate.getCanonicalPath().contains(parent.getCanonicalPath())) {
                        manifestEntries.add(ClassPathManifestEntry.of(candidate));
                    }
                }
            }
            return Collections.unmodifiableSet(manifestEntries);
        }
        catch (Exception ex) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Failed to load manifest entries from jar file '{}': ", jar, ex);
            }
            return Collections.emptySet();
        }
    }

    /// Find all resources that match the given location pattern via the Ant-style
    /// [PathMatcher][#getPathMatcher()].
    ///
    /// Supports resources in OSGi bundles, JBoss VFS, jar files, zip files,
    /// and file systems.
    ///
    /// @param locationPattern the location pattern to match
    /// @return the result as Resource array
    /// @throws IOException in case of I/O errors
    /// @see #determineRootDir(String)
    /// @see #resolveRootDirResource(Resource)
    /// @see #isJarResource(Resource)
    /// @see #doFindPathMatchingJarResources(Resource, URL, String)
    /// @see #doFindPathMatchingFileResources(Resource, String)
    /// @see PathMatcher
    protected Resource[] findPathMatchingResources(String locationPattern) throws IOException {
        String rootDirPath = determineRootDir(locationPattern);
        String subPattern = locationPattern.substring(rootDirPath.length());

        // Look for pre-cached root dir resources, either a direct match or
        // a match for a parent directory in the same classpath locations.
        Resource[] rootDirResources = this.rootDirCache.get(rootDirPath);
        String actualRootPath = null;
        if (rootDirResources == null) {
            // No direct match -> search for a common parent directory match
            // (cached based on repeated searches in the same base location,
            // in particular for different root directories in the same jar).
            String commonPrefix = null;
            String existingPath = null;
            boolean commonUnique = true;
            for (String path : this.rootDirCache.keySet()) {
                String currentPrefix = null;
                for (int i = 0; i < path.length(); i++) {
                    if (i == rootDirPath.length() || path.charAt(i) != rootDirPath.charAt(i)) {
                        currentPrefix = path.substring(0, path.lastIndexOf('/', i - 1) + 1);
                        break;
                    }
                }
                if (currentPrefix != null) {
                    if (checkPathWithinPackage(path.substring(currentPrefix.length()))) {
                        // A prefix match found, potentially to be turned into a common parent cache entry.
                        if (commonPrefix == null || !commonUnique || currentPrefix.length() > commonPrefix.length()) {
                            commonPrefix = currentPrefix;
                            existingPath = path;
                        }
                        else if (currentPrefix.equals(commonPrefix)) {
                            commonUnique = false;
                        }
                    }
                }
                else if (actualRootPath == null || path.length() > actualRootPath.length()) {
                    // A direct match found for a parent directory -> use it.
                    rootDirResources = this.rootDirCache.get(path);
                    actualRootPath = path;
                }
            }
            if (rootDirResources == null && StringUtils.hasLength(commonPrefix)) {
                // Try common parent directory as long as it points to the same classpath locations.
                rootDirResources = getResources(commonPrefix);
                Resource[] existingResources = this.rootDirCache.get(existingPath);
                if (existingResources != null && rootDirResources.length == existingResources.length) {
                    // Replace existing subdirectory cache entry with common parent directory,
                    // avoiding repeated determination of root directories in the same jar.
                    this.rootDirCache.remove(existingPath);
                    this.rootDirCache.put(commonPrefix, rootDirResources);
                    actualRootPath = commonPrefix;
                }
                else if (commonPrefix.equals(rootDirPath)) {
                    // The identified common directory is equal to the currently requested path ->
                    // worth caching specifically, even if it cannot replace the existing sub-entry.
                    this.rootDirCache.put(rootDirPath, rootDirResources);
                }
                else {
                    // Mismatch: parent directory points to more classpath locations.
                    rootDirResources = null;
                }
            }
            if (rootDirResources == null) {
                // Lookup for specific directory, creating a cache entry for it.
                rootDirResources = getResources(rootDirPath);
                if (this.useCaches) {
                    this.rootDirCache.put(rootDirPath, rootDirResources);
                }
            }
        }

        Set<Resource> result = new LinkedHashSet<>(64);
        for (Resource rootDirResource : rootDirResources) {
            if (actualRootPath != null && actualRootPath.length() < rootDirPath.length()) {
                // Create sub-resource for requested sub-location from cached common root directory.
                rootDirResource = rootDirResource.createRelative(rootDirPath.substring(actualRootPath.length()));
            }
            rootDirResource = resolveRootDirResource(rootDirResource);
            URL rootDirUrl = rootDirResource.getURL();
            if (equinoxResolveMethod != null && rootDirUrl.getProtocol().startsWith("bundle")) {
                URL resolvedUrl;
                try {
                    resolvedUrl = (URL) equinoxResolveMethod.invoke(null, rootDirUrl);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
                if (resolvedUrl != null) {
                    rootDirUrl = resolvedUrl;
                }
                rootDirResource = new UrlResource(rootDirUrl);
            }
            if (rootDirUrl.getProtocol().startsWith(ResourceUtils.URL_PROTOCOL_VFS)) {
                result.addAll(VfsResourceMatchingDelegate.findMatchingResources(rootDirUrl, subPattern, getPathMatcher()));
            }
            else if (ResourceUtils.isJarURL(rootDirUrl) || isJarResource(rootDirResource)) {
                result.addAll(doFindPathMatchingJarResources(rootDirResource, rootDirUrl, subPattern));
            }
            else {
                result.addAll(doFindPathMatchingFileResources(rootDirResource, subPattern));
            }
        }
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Resolved location pattern [{}] to resources {}", locationPattern, result);
        }
        return result.toArray(EMPTY_RESOURCE_ARRAY);
    }

    /// Determine the root directory for the given location.
    ///
    /// Used for determining the starting point for file matching, resolving the
    /// root directory location to be passed into [#getResources(String)],
    /// with the remainder of the location to be used as the sub pattern.
    ///
    /// Will return "/WEB-INF/" for the location "/WEB-INF/*.xml", for example.
    ///
    /// @param location the location to check
    /// @return the part of the location that denotes the root directory
    /// @see #findPathMatchingResources(String)
    protected String determineRootDir(String location) {
        int prefixEnd = location.indexOf(':') + 1;
        int rootDirEnd = location.length();
        while (rootDirEnd > prefixEnd && getPathMatcher().isPattern(location.substring(prefixEnd, rootDirEnd))) {
            rootDirEnd = location.lastIndexOf('/', rootDirEnd - 2) + 1;
        }
        if (rootDirEnd == 0) {
            rootDirEnd = prefixEnd;
        }
        return location.substring(0, rootDirEnd);
    }

    /// Resolve the supplied root directory resource for path matching.
    ///
    /// By default, [#findPathMatchingResources(String)] resolves Equinox
    /// OSGi "bundleresource:" and "bundleentry:" URLs into standard jar file URLs
    /// that will be traversed using Spring's standard jar file traversal algorithm.
    ///
    /// For any custom resolution, override this template method and replace the
    /// supplied resource handle accordingly.
    ///
    /// The default implementation of this method returns the supplied resource
    /// unmodified.
    ///
    /// @param original the resource to resolve
    /// @return the resolved resource (may be identical to the supplied resource)
    /// @throws IOException in case of resolution failure
    /// @see #findPathMatchingResources(String)
    protected Resource resolveRootDirResource(Resource original) throws IOException {
        return original;
    }

    /// Determine if the given resource handle indicates a jar resource that the
    /// [#doFindPathMatchingJarResources] method can handle.
    ///
    /// [#findPathMatchingResources(String)] delegates to
    /// [#isJarURL(URL)] to determine whether the given URL
    /// points to a resource in a jar file, and only invokes this method as a fallback.
    ///
    /// This template method therefore allows for detecting further kinds of
    /// jar-like resources &mdash; for example, via `instanceof` checks on
    /// the resource handle type.
    ///
    /// The default implementation of this method returns `false`.
    ///
    /// @param resource the resource handle to check (usually the root directory
    /// to start path matching from)
    /// @return `true` if the given resource handle indicates a jar resource
    /// @throws IOException in case of I/O errors
    /// @see #findPathMatchingResources(String)
    /// @see #doFindPathMatchingJarResources(Resource, URL, String)
    /// @see ResourceUtils#isJarURL
    protected boolean isJarResource(Resource resource) throws IOException {
        return false;
    }

    /// Find all resources in jar files that match the given location pattern
    /// via the Ant-style [PathMatcher][#getPathMatcher()].
    ///
    /// @param rootDirResource the root directory as Resource
    /// @param rootDirUrl the pre-resolved root directory URL
    /// @param subPattern the sub pattern to match (below the root directory)
    /// @return a mutable Set of matching Resource instances
    /// @throws IOException in case of I/O errors
    /// @since 4.3
    /// @see java.net.JarURLConnection
    /// @see PathMatcher
    protected Set<Resource> doFindPathMatchingJarResources(Resource rootDirResource, URL rootDirUrl, String subPattern)
            throws IOException {

        String jarFileUrl = null;
        String rootEntryPath = "";

        String urlFile = rootDirUrl.getFile();
        int separatorIndex = urlFile.indexOf(ResourceUtils.WAR_URL_SEPARATOR);
        if (separatorIndex == -1) {
            separatorIndex = urlFile.indexOf(ResourceUtils.JAR_URL_SEPARATOR);
        }
        if (separatorIndex >= 0) {
            jarFileUrl = urlFile.substring(0, separatorIndex);
            rootEntryPath = urlFile.substring(separatorIndex + 2);  // both separators are 2 chars
            NavigableSet<String> entriesCache = this.jarEntriesCache.get(jarFileUrl);
            if (entriesCache != null) {
                Set<Resource> result = new LinkedHashSet<>(64);
                // Clean root entry path to match jar entries format without "!" separators
                rootEntryPath = rootEntryPath.replace(ResourceUtils.JAR_URL_SEPARATOR, "/");
                // Search sorted entries from first entry with rootEntryPath prefix
                boolean rootEntryPathFound = false;
                for (String entryPath : entriesCache.tailSet(rootEntryPath, false)) {
                    if (!entryPath.startsWith(rootEntryPath)) {
                        // We are beyond the potential matches in the current TreeSet.
                        break;
                    }
                    rootEntryPathFound = true;
                    String relativePath = entryPath.substring(rootEntryPath.length());
                    if (getPathMatcher().match(subPattern, relativePath)) {
                        result.add(rootDirResource.createRelative(relativePath));
                    }
                }
                if (rootEntryPathFound) {
                    return result;
                }
            }
        }

        URLConnection con = rootDirUrl.openConnection();
        JarFile jarFile;
        boolean closeJarFile;

        if (con instanceof JarURLConnection jarCon) {
            // Should usually be the case for traditional JAR files.
            if (!this.useCaches) {
                jarCon.setUseCaches(false);
            }
            try {
                jarFile = jarCon.getJarFile();
                jarFileUrl = jarCon.getJarFileURL().toExternalForm();
                JarEntry jarEntry = jarCon.getJarEntry();
                rootEntryPath = (jarEntry != null ? jarEntry.getName() : "");
                closeJarFile = !jarCon.getUseCaches();
            }
            catch (ZipException | FileNotFoundException | NoSuchFileException ex) {
                // Happens in case of a non-jar file or in case of a cached root directory
                // without the specific subdirectory present, respectively.
                return Collections.emptySet();
            }
        }
        else {
            // No JarURLConnection -> need to resort to URL file parsing.
            // We'll assume URLs of the format "jar:path!/entry", with the protocol
            // being arbitrary as long as following the entry format.
            // We'll also handle paths with and without leading "file:" prefix.
            try {
                if (jarFileUrl != null) {
                    jarFile = getJarFile(jarFileUrl);
                }
                else {
                    jarFile = new JarFile(urlFile);
                    jarFileUrl = urlFile;
                    rootEntryPath = "";
                }
                closeJarFile = true;
            }
            catch (ZipException ex) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Skipping invalid jar class path entry [{}]", urlFile);
                }
                return Collections.emptySet();
            }
        }

        try {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Looking for matching resources in jar file [{}]", jarFileUrl);
            }
            if (StringUtils.hasLength(rootEntryPath) && !rootEntryPath.endsWith("/")) {
                // Root entry path must end with slash to allow for proper matching.
                // The Sun JRE does not return a slash here, but BEA JRockit does.
                rootEntryPath = rootEntryPath + "/";
            }
            Set<Resource> result = new LinkedHashSet<>(64);
            NavigableSet<String> entriesCache = new TreeSet<>();
            Iterator<String> entryIterator = jarFile.stream().map(JarEntry::getName).sorted().iterator();
            while (entryIterator.hasNext()) {
                String entryPath = entryIterator.next();
                int entrySeparatorIndex = entryPath.indexOf(ResourceUtils.JAR_URL_SEPARATOR);
                if (entrySeparatorIndex >= 0) {
                    entryPath = entryPath.substring(entrySeparatorIndex + ResourceUtils.JAR_URL_SEPARATOR.length());
                }
                entriesCache.add(entryPath);
                if (entryPath.startsWith(rootEntryPath)) {
                    String relativePath = entryPath.substring(rootEntryPath.length());
                    if (getPathMatcher().match(subPattern, relativePath)) {
                        result.add(rootDirResource.createRelative(relativePath));
                    }
                }
            }
            if (this.useCaches) {
                // Cache jar entries in TreeSet for efficient searching on re-encounter.
                this.jarEntriesCache.put(jarFileUrl, entriesCache);
            }
            return result;
        }
        finally {
            if (closeJarFile) {
                jarFile.close();
            }
        }
    }

    /**
     * Resolve the given jar file URL into a JarFile object.
     */
    protected JarFile getJarFile(String jarFileUrl) throws IOException {
        if (jarFileUrl.startsWith(ResourceUtils.FILE_URL_PREFIX)) {
            try {
                return new JarFile(ResourceUtils.toURI(jarFileUrl).getSchemeSpecificPart());
            }
            catch (URISyntaxException ex) {
                // Fallback for URLs that are not valid URIs (should hardly ever happen).
                return new JarFile(jarFileUrl.substring(ResourceUtils.FILE_URL_PREFIX.length()));
            }
        }
        else {
            return new JarFile(jarFileUrl);
        }
    }

    /// Find all resources in the file system of the supplied root directory that
    /// match the given location sub pattern via the Ant-style [PathMatcher][#getPathMatcher()].
    ///
    /// @param rootDirResource the root directory as a Resource
    /// @param subPattern the sub pattern to match (below the root directory)
    /// @return a mutable Set of matching Resource instances
    /// @throws IOException in case of I/O errors
    /// @see PathMatcher
    protected Set<Resource> doFindPathMatchingFileResources(Resource rootDirResource, String subPattern)
            throws IOException {

        Set<Resource> result = new LinkedHashSet<>(64);
        URI rootDirUri;
        try {
            rootDirUri = rootDirResource.getURI();
        }
        catch (Exception ex) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Failed to resolve directory [{}] as URI:", rootDirResource, ex);
            }
            return result;
        }

        Path rootPath = null;
        if (rootDirUri.isAbsolute() && !rootDirUri.isOpaque()) {
            // Prefer Path resolution from URI if possible
            try {
                try {
                    rootPath = Path.of(rootDirUri);
                }
                catch (FileSystemNotFoundException ex) {
                    // If the file system was not found, assume it's a custom file system that needs to be installed.
                    FileSystems.newFileSystem(rootDirUri, Map.of(), ClassUtils.getDefaultClassLoader());
                    rootPath = Path.of(rootDirUri);
                }
            }
            catch (Exception ex) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Failed to resolve {} in file system:", rootDirUri, ex);
                }
                // Fallback via Resource.getFile() below
            }
        }

        if (rootPath == null) {
            // Resource.getFile() resolution as a fallback -
            // for custom URI formats and custom Resource implementations
            try {
                rootPath = Path.of(rootDirResource.getFile().getAbsolutePath());
            }
            catch (FileNotFoundException ex) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Cannot search for matching files underneath {} in the file system:",
                            rootDirResource, ex);
                }
                return result;
            }
            catch (Exception ex) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Failed to resolve {} in the file system:", rootDirResource, ex);
                }
                return result;
            }
        }

        if (!Files.exists(rootPath)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Skipping search for files matching pattern [{}]: directory [{}] does not exist",
                        subPattern, rootPath.toAbsolutePath());
            }
            return result;
        }

        String rootDir = StringUtils.cleanPath(rootPath.toString());
        if (!rootDir.endsWith("/")) {
            rootDir += "/";
        }

        Path rootPathForPattern = rootPath;
        String resourcePattern = rootDir + StringUtils.cleanPath(subPattern);
        Predicate<Path> isMatchingFile = path -> (!path.equals(rootPathForPattern) &&
                getPathMatcher().match(resourcePattern, StringUtils.cleanPath(path.toString())));

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Searching directory [{}] for files matching pattern [{}]",
                    rootPath.toAbsolutePath(), subPattern);
        }

        try (Stream<Path> files = Files.walk(rootPath, FileVisitOption.FOLLOW_LINKS)) {
            files.filter(isMatchingFile).sorted().map(FileSystemResource::new).forEach(result::add);
        }
        catch (Exception ex) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Failed to search in directory [{}] for files matching pattern [{}]:",
                        rootPath.toAbsolutePath(), subPattern, ex);
            }
        }
        return result;
    }

    /// Resolve the given location pattern into `Resource` objects for all
    /// matching resources found in the module path.
    ///
    /// The location pattern may be an explicit resource path such as
    /// `"com/example/config.xml"` or a pattern such as
    /// <code>"com/example/**&#47;config-*.xml"</code> to be matched using the
    /// configured [PathMatcher][#getPathMatcher()].
    ///
    /// The default implementation scans all modules in the {@linkplain ModuleLayer#boot()
    ///  boot layer}, excluding {@linkplain ModuleFinder#ofSystem() system modules}.
    ///
    /// @param locationPattern the location pattern to resolve
    /// @return a modifiable `Set` containing the corresponding `Resource`
    /// objects
    /// @throws IOException in case of I/O errors
    /// @since 6.0
    /// @see ModuleLayer#boot()
    /// @see ModuleFinder#ofSystem()
    /// @see ModuleReader
    /// @see PathMatcher#match(String, String)
    protected Set<Resource> findAllModulePathResources(String locationPattern) throws IOException {
        Set<Resource> result = new LinkedHashSet<>(64);

        // Skip scanning the module path when running in a native image.
        if (NativeDetector.inNativeImage()) {
            return result;
        }

        String resourcePattern = stripLeadingSlash(locationPattern);
        Predicate<String> resourcePatternMatches = (getPathMatcher().isPattern(resourcePattern) ?
                path -> getPathMatcher().match(resourcePattern, path) :
                resourcePattern::equals);

        try {
            ModuleLayer.boot().configuration().modules().stream()
                    .filter(isNotSystemModule)
                    .forEach(resolvedModule -> {
                        // NOTE: a ModuleReader and a Stream returned from ModuleReader.list() must be closed.
                        try (ModuleReader moduleReader = resolvedModule.reference().open();
                             Stream<String> names = moduleReader.list()) {
                            names.filter(resourcePatternMatches)
                                    .map(name -> findResource(moduleReader, name))
                                    .filter(Objects::nonNull)
                                    .forEach(result::add);
                        }
                        catch (IOException ex) {
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("Failed to read contents of module [{}]", resolvedModule, ex);
                            }
                            throw new UncheckedIOException(ex);
                        }
                    });
        }
        catch (UncheckedIOException ex) {
            // Unwrap IOException to conform to this method's contract.
            throw ex.getCause();
        }

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Resolved module-path location pattern [{}] to resources {}", resourcePattern, result);
        }
        return result;
    }

    @Nullable
    private Resource findResource(ModuleReader moduleReader, String name) {
        try {
            return moduleReader.find(name)
                    .map(this::convertModuleSystemURI)
                    .orElse(null);
        }
        catch (Exception ex) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Failed to find resource [{}] in module path", name, ex);
            }
            return null;
        }
    }

    /// If it's a "file:" URI, use [FileSystemResource] to avoid duplicates
    /// for the same path discovered via class path scanning.
    private Resource convertModuleSystemURI(URI uri) {
        return (ResourceUtils.URL_PROTOCOL_FILE.equals(uri.getScheme()) ?
                new FileSystemResource(uri.getPath()) : UrlResource.from(uri));
    }

    private static String stripLeadingSlash(String path) {
        return (path.startsWith("/") ? path.substring(1) : path);
    }

    private static boolean checkPathWithinPackage(String path) {
        return (path.contains("/") && !path.contains(ResourceUtils.JAR_URL_SEPARATOR));
    }

    /// Inner delegate class, avoiding a hard JBoss VFS API dependency at runtime.
    private static class VfsResourceMatchingDelegate {

        public static Set<Resource> findMatchingResources(
                URL rootDirUrl, String locationPattern, PathMatcher pathMatcher) throws IOException {

            Object root = VfsPatternUtils.findRoot(rootDirUrl);
            PatternVirtualFileVisitor visitor =
                    new PatternVirtualFileVisitor(VfsPatternUtils.getPath(root), locationPattern, pathMatcher);
            VfsPatternUtils.visit(root, visitor);
            return visitor.getResources();
        }
    }

    /// VFS visitor for path matching purposes.
    @SuppressWarnings("unused")
    private static class PatternVirtualFileVisitor implements InvocationHandler {

        private final String subPattern;

        private final PathMatcher pathMatcher;

        private final String rootPath;

        private final Set<Resource> resources = new LinkedHashSet<>(64);

        public PatternVirtualFileVisitor(String rootPath, String subPattern, PathMatcher pathMatcher) {
            this.subPattern = subPattern;
            this.pathMatcher = pathMatcher;
            this.rootPath = (rootPath.isEmpty() || rootPath.endsWith("/") ? rootPath : rootPath + "/");
        }

        @Override
        @Nullable
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            if (Object.class == method.getDeclaringClass()) {
                switch (methodName) {
                    case "equals" -> {
                        // Only consider equal when proxies are identical.
                        return (proxy == args[0]);
                    }
                    case "hashCode" -> {
                        return System.identityHashCode(proxy);
                    }
                }
            }
            return switch (methodName) {
                case "getAttributes" -> getAttributes();
                case "visit" -> {
                    visit(args[0]);
                    yield null;
                }
                case "toString" -> toString();
                default -> throw new IllegalStateException("Unexpected method invocation: " + method);
            };
        }

        public void visit(Object vfsResource) {
            if (this.pathMatcher.match(this.subPattern,
                    VfsPatternUtils.getPath(vfsResource).substring(this.rootPath.length()))) {
                this.resources.add(new VfsResource(vfsResource));
            }
        }

        @Nullable
        public Object getAttributes() {
            return VfsPatternUtils.getVisitorAttributes();
        }

        public Set<Resource> getResources() {
            return this.resources;
        }

        public int size() {
            return this.resources.size();
        }

        @Override
        public String toString() {
            return "sub-pattern: " + this.subPattern + ", resources: " + this.resources;
        }
    }

    /// A single `Class-Path` manifest entry.
    private record ClassPathManifestEntry(Resource resource, @Nullable Resource alternative) {

        private static final String JARFILE_URL_PREFIX = ResourceUtils.JAR_URL_PREFIX + ResourceUtils.FILE_URL_PREFIX;

        static ClassPathManifestEntry of(File file) throws MalformedURLException {
            String path = fixPath(file.getAbsolutePath());
            Resource resource = asJarFileResource(path);
            Resource alternative = createAlternative(path);
            return new ClassPathManifestEntry(resource, alternative);
        }

        private static String fixPath(String path) {
            int prefixIndex = path.indexOf(':');
            if (prefixIndex == 1) {
                // Possibly a drive prefix on Windows (for example, "c:"), so we prepend a slash
                // and convert the drive letter to uppercase for consistent duplicate detection.
                path = "/" + StringUtils.capitalize(path);
            }
            // Since '#' can appear in directories/filenames, java.net.URL should not treat it as a fragment
            return StringUtils.replace(path, "#", "%23");
        }

        /// Return an alternative form of the resource, i.e. with or without a leading slash.
        /// @param path the file path (with or without a leading slash)
        /// @return the alternative form or `null`
        @Nullable
        private static Resource createAlternative(String path) {
            try {
                String alternativePath = path.startsWith("/") ? path.substring(1) : "/" + path;
                return asJarFileResource(alternativePath);
            }
            catch (MalformedURLException ex) {
                return null;
            }
        }

        private static Resource asJarFileResource(String path) throws MalformedURLException {
            return new UrlResource(JARFILE_URL_PREFIX + path + ResourceUtils.JAR_URL_SEPARATOR);
        }
    }
}
