package io.polyaxis.network;

/// Dubbo Constants.
///
/// @author github.com/MoritzArena
/// @date 2025/07/06
/// @since 1.0
public class DubboConstants {

    private DubboConstants() {
    }

    public static final Integer SERVER_DEFAULT_OFFSET = 1000;

    /// Dubbo properties key
    public enum Properties {
        ;

        private static final String SERVER_KEY = "network.dubbo.server.";

        /// `network.dubbo.server.port.offset`
        public static final String SERVER_PORT_OFFSET = SERVER_KEY + "port.offset";

        /// `network.dubbo.server.tls.timeout`
        public static final String SERVER_TLS_TIMEOUT = SERVER_KEY + "tls.timeout";

        /// `network.dubbo.server.tls.actives`
        public static final String SERVER_TLS_ACTIVES = SERVER_KEY + "tls.actives";
    }
}
