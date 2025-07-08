package io.polyaxis.api.utils.context;

/// A convenient tool to get property or env value.
///
/// @author github.com/MoritzArena
/// @date 2025/07/05
/// @since 1.0
public final class PropertyUtils {

    private PropertyUtils() {
    }
    
    private static final String PROCESSORS_ENV_NAME = "POLY_AXIS_APP_PROCESSORS";
    
    private static final String PROCESSORS_PROP_NAME = "io.polyaxis.app.processors";
    
    /// Get system env or property value.
    ///
    /// If [#getenv()] has no value for `envName`, return [#getProperty(String)].
    public static String getProperty(String propertyName, String envName) {
        return System.getenv().getOrDefault(envName, System.getProperty(propertyName));
    }
    
    /// Get system env or property value.
    ///
    /// If [#getenv()] has no value for `envName`,
    /// return [#getProperty(String,String)] or `defaultValue`.
    public static String getProperty(String propertyName, String envName, String defaultValue) {
        return System.getenv().getOrDefault(envName, System.getProperty(propertyName, defaultValue));
    }
    
    /// Get processors count maybe preset by env or property.
    public static int getProcessorsCount() {
        int processorsCount = 0;
        String processorsCountPreSet = getProperty(PROCESSORS_PROP_NAME, PROCESSORS_ENV_NAME);
        if (processorsCountPreSet != null) {
            try {
                processorsCount = Integer.parseInt(processorsCountPreSet);
            } catch (NumberFormatException ignored) {
            }
        }
        if (processorsCount <= 0) {
            processorsCount = Runtime.getRuntime().availableProcessors();
        }
        return processorsCount;
    }
}
