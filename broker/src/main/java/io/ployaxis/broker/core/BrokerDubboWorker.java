package io.ployaxis.broker.core;

import io.polyaxis.api.utils.context.EnvironmentUtils;
import io.polyaxis.api.utils.misc.LoggerScope;
import io.polyaxis.network.DubboConstants;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.slf4j.Logger;

/// Broker Dubbo Worker.
///
/// @author github.com/MoritzArena
/// @date 2025/07/08
/// @since 1.0
public class BrokerDubboWorker {

    private static final Logger LOGGER = LoggerScope.DUBBO;

    private static final Integer PORT_OFFSET = Integer.getInteger(
            DubboConstants.Properties.SERVER_PORT_OFFSET, DubboConstants.SERVER_DEFAULT_OFFSET);

    private final DubboBootstrap bootstrap;

    private final BrokerDubboClient brokerDubboClient;

    private final BrokerDubboServer brokerDubboServer;

    public BrokerDubboWorker() {
        this.bootstrap = DubboBootstrap.getInstance();
        // init dubbo application config
        final var appConfig = new ApplicationConfig(BrokerConstants.DUBBO_APPLICATION_NAME);
        appConfig.setQosEnable(false);
        appConfig.setQosPort(-1);
        appConfig.setQosAcceptForeignIp(false);
        this.bootstrap.application(appConfig);
        // build dubbo client and server
        this.brokerDubboClient = new BrokerDubboClient(this.bootstrap, PORT_OFFSET);
        this.brokerDubboServer = new BrokerDubboServer(this.bootstrap, this.getPort());
        // start broker dubbo client and server
        this.start0();
        LOGGER.info("broker dubbo server and client started successfully");
    }

    public void stop() {
        this.stop0();
    }

    private void start0() {
        this.bootstrap.start();
    }

    private void stop0() {
        this.bootstrap.stop();
    }

    public Integer getPort() {
        return EnvironmentUtils.getPort() + PORT_OFFSET;
    }
}
