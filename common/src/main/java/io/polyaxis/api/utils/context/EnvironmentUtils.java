package io.polyaxis.api.utils.context;

import io.polyaxis.api.utils.concurrency.ThreadUtils;
import io.polyaxis.api.utils.documentation.Unsafe;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import java.util.Properties;

/// MicroProfile Environment Utils.
///
/// @author github.com/MoritzArena
/// @date 2025/07/08
/// @since 1.0
public class EnvironmentUtils {

    /* ------------------------------------------------------------------ */
    /*                     section: static configuration                  */
    /* ------------------------------------------------------------------ */

    /** Global MicroProfile Config instance (read-only). */
    private static final Config CONFIG = ConfigProvider.getConfig();

    /* address key */
    private static final String SERVER_ADDRESS_KEY = "quarkus.http.host";

    /* port key */
    private static final String SERVER_PORT_KEY = "quarkus.http.port";

    private static final String DEFAULT_SERVER_ADDRESS = "127.0.0.1";

    private static final int DEFAULT_SERVER_PORT = 7777;

    /* cached values */
    private static volatile int port = -1;

    private static volatile String address = "";

    /* processors */
    private static final String AVAILABLE_PROCESSORS_BASIC = "io.polyaxis.sys.processors";

    private EnvironmentUtils() {
    }

    /* ------------------------------------------------------------------ */
    /*                     section: generic property helpers              */
    /* ------------------------------------------------------------------ */

    public static String getProperty(String key) {
        return CONFIG.getOptionalValue(key, String.class).orElse(null);
    }

    public static String getProperty(String key, String defaultValue) {
        return CONFIG.getOptionalValue(key, String.class).orElse(defaultValue);
    }

    public static <T> T getProperty(String key, Class<T> targetType) {
        return CONFIG.getOptionalValue(key, targetType).orElse(null);
    }

    public static <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        return CONFIG.getOptionalValue(key, targetType).orElse(defaultValue);
    }

    public static Config getConfig() {
        return CONFIG;
    }

    /**
     * Dump all visible MicroProfile Config entries into a {@link Properties} object.
     */
    @Unsafe
    public static Properties getProperties() {
        final Properties props = new Properties();
        CONFIG.getPropertyNames().forEach(
                name -> props.setProperty(name, CONFIG.getValue(name, String.class))
        );
        return props;
    }

    /* ------------------------------------------------------------------ */
    /*                section: available processor helpers                */
    /* ------------------------------------------------------------------ */

    /**
     * Return configured processor count or a runtime-sensible default.
     */
    public static int getAvailableProcessors() {
        int value = getProperty(
                AVAILABLE_PROCESSORS_BASIC,
                Integer.class,
                ThreadUtils.getSuitableThreadCount(1)
        );
        return Math.max(value, 1);
    }

    public static int getAvailableProcessors(int multiple) {
        if (multiple < 1) throw new IllegalArgumentException("Multiple must be ≥ 1");
        Integer fixed = getProperty(AVAILABLE_PROCESSORS_BASIC, Integer.class);
        return (fixed != null && fixed > 0)
                ? fixed * multiple
                : ThreadUtils.getSuitableThreadCount(multiple);
    }

    public static int getAvailableProcessors(double scale) {
        if (scale < 0 || scale > 1)
            throw new IllegalArgumentException("Scale must be between 0 and 1");
        double result = getProperty(
                AVAILABLE_PROCESSORS_BASIC,
                Integer.class,
                ThreadUtils.getSuitableThreadCount(1)
        ) * scale;
        return result > 1 ? (int) result : 1;
    }

    /* ------------------------------------------------------------------ */
    /*                       section: address / port                      */
    /* ------------------------------------------------------------------ */

    public static String getAddress() {
        if (address.isEmpty()) {
            address = CONFIG.getOptionalValue(SERVER_ADDRESS_KEY, String.class)
                    .orElse(DEFAULT_SERVER_ADDRESS);
        }
        return address;
    }

    public static int getPort() {
        if (port == -1) {
            port = CONFIG.getOptionalValue(SERVER_PORT_KEY, Integer.class)
                    .orElse(DEFAULT_SERVER_PORT);
        }
        return port;
    }

    public static void setPort(int port) {
        EnvironmentUtils.port = port;
    }
}
