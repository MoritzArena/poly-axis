package io.polyaxis.dispatcher.core;

import io.polyaxis.api.utils.misc.LoggerScope;
import io.polyaxis.network.DubboConstants;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.slf4j.Logger;

/// Application dispatcher dubbo worker.
///
/// @author github.com/MoritzArena
/// @date 2025/07/06
/// @since 1.0
public class DispatcherDubboWorker {

    private static final Logger LOGGER = LoggerScope.DUBBO;

    private static final Integer PORT_OFFSET = Integer.getInteger(
            DubboConstants.Properties.SERVER_PORT_OFFSET, DubboConstants.SERVER_DEFAULT_OFFSET);

    private final DubboBootstrap bootstrap;

    private final DispatcherDubboClient dispatcherDubboClient;

    private final DispatcherDubboServer dispatcherDubboServer;

    public DispatcherDubboWorker() {
        this.bootstrap = DubboBootstrap.getInstance();
        // init dubbo application config
        final var appConfig = new ApplicationConfig(DispatcherConstants.DUBBO_APPLICATION_NAME);
        appConfig.setQosEnable(false);
        appConfig.setQosPort(-1);
        appConfig.setQosAcceptForeignIp(false);
        this.bootstrap.application(appConfig);
        // build dubbo client and server
        this.dispatcherDubboClient = new DispatcherDubboClient(bootstrap, PORT_OFFSET);
        this.dispatcherDubboServer = new DispatcherDubboServer(bootstrap, this.getPort());
        // start broker dubbo client and server
        this.start0();
        LOGGER.info("dispatcher dubbo server and client started successfully");
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
