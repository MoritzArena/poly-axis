package io.polyaxis.dispatcher.core;

import io.polyaxis.api.utils.misc.LoggerScope;
import io.polyaxis.network.dispatcher.DispatcherService;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.slf4j.Logger;

/// Application dispatcher dubbo server.
///
/// @author github.com/MoritzArena
/// @date 2025/07/06
/// @since 1.0
final class DispatcherDubboServer {

    private static final Logger LOGGER = LoggerScope.DUBBO;

    private final ServiceConfig<DispatcherService> provider;

    public DispatcherDubboServer(
            final DubboBootstrap bootstrap,
            final Integer actualPort
    ) {
        this.provider = this.constructServiceConfig();
        // usually use actualPort(port + 1000) as broker dubbo server port
        bootstrap.protocol(new ProtocolConfig(CommonConstants.TRIPLE, actualPort)).service(this.provider);
        LOGGER.info("dispatcher dubbo(tri://) server has been built at port {}, waiting for unified start", actualPort);
    }

    /// construct [DispatcherService]s config
    private ServiceConfig<DispatcherService> constructServiceConfig() {
        final ServiceConfig<DispatcherService> serviceConfig = new ServiceConfig<>();
        serviceConfig.setGroup(DispatcherConstants.DUBBO_SERVICE_GROUP);
        serviceConfig.setVersion(DispatcherConstants.DUBBO_SERVICE_VERSION);
        serviceConfig.setInterface(DispatcherService.class);
        serviceConfig.setRef(new DispatcherServiceProvider());
        serviceConfig.setRegister(false);
        serviceConfig.setExport(true);
        return serviceConfig;
    }
}
