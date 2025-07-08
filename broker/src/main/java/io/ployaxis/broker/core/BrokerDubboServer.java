package io.ployaxis.broker.core;

import io.polyaxis.api.utils.misc.LoggerScope;
import io.polyaxis.network.broker.BrokerService;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.slf4j.Logger;

/// Broker Dubbo Server.
///
/// @author github.com/MoritzArena
/// @date 2025/07/08
/// @since 1.0
public class BrokerDubboServer {

    private static final Logger LOGGER = LoggerScope.DUBBO;

    public BrokerDubboServer(
            final DubboBootstrap bootstrap,
            final Integer actualPort
    ) {
        final var provider = this.constructServiceConfig();
        // usually use actualPort(port + 1000) as broker dubbo server port
        bootstrap.protocol(new ProtocolConfig(CommonConstants.TRIPLE, actualPort)).service(provider);
        LOGGER.info("broker dubbo(tri://) server has been built at port {}, waiting for unified start", actualPort);
    }

    /// construct [BrokerService]s config
    private ServiceConfig<BrokerService> constructServiceConfig() {
        final ServiceConfig<BrokerService> serviceConfig = new ServiceConfig<>();
        serviceConfig.setInterface(BrokerService.class);
        serviceConfig.setRef(new BrokerServiceProvider());
        return serviceConfig;
    }
}
