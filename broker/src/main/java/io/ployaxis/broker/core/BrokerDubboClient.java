package io.ployaxis.broker.core;

import io.ployaxis.broker.utils.DispatcherConfigReader;
import io.polyaxis.api.utils.misc.LoggerScope;
import io.polyaxis.network.dispatcher.BrokerRegisterReq;
import io.polyaxis.network.dispatcher.DispatcherService;
import io.smallrye.mutiny.Uni;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/// **Broker** client is a type of _dubbo consumer_, charge on consuming register and
/// refresh service provided by each dispatcher server. hinge upon this, broker can
/// expose self's all plugin capabilities to dispatchers.
///
/// so, each broker will hold all dispatchers' connection in memory, then could
/// send requests regularly to each dispatcher.
///
/// @author github.com/MoritzArena
/// @date 2025/07/08
/// @since 1.0
public class BrokerDubboClient {

    private static final Logger LOGGER = LoggerScope.DUBBO;

    private final List<ReferenceConfig<DispatcherService>> dispatcherProviders = new ArrayList<>(1 << 3);

    private final Integer portOffset;

    @SuppressWarnings("rawtypes")
    public BrokerDubboClient(
            final DubboBootstrap bootstrap,
            final Integer portOffset
    ) {
        this.portOffset = portOffset;
        // construct consumer reference configs
        this.constructReferenceConfigs();
        LOGGER.info("successfully build {} dubbo(tri://) consumer client(s), waiting for unified start", dispatcherProviders.size());
        // dubbo bootstrap
        bootstrap.references(dispatcherProviders.stream()
                .map(item -> (ReferenceConfig) item).toList());
        // start scheduled tasks
        this.scheduledTasks();
    }

    private void scheduledTasks() {
        for (final var provider : this.dispatcherProviders) {
            LOGGER.info("start register broker to {}", provider.getUrl());
            provider.get()
                    .registerBroker(Uni.createFrom().item(BrokerRegisterReq.newBuilder().build()))
                    .subscribe()
                    .with(
                            res -> LOGGER.info("consumeOneToMany get response: {}", res),
                            err -> LOGGER.error("consumeOneToMany failed!", err)
                    );
        }
    }

    /// construct [ReferenceConfig]s
    private void constructReferenceConfigs()  {
        final var dispatcherAddresses = DispatcherConfigReader.getDispatcherAddresses();
        for (final var dispatcherAddress : dispatcherAddresses) {
            final var dispatcherIp = dispatcherAddress.getFirst();
            final var dispatcherPort = dispatcherAddress.getSecond();
            // craft dubbo consumer reference config
            final var dispatcherReferenceConfig =
                    this.constructReferenceConfig(dispatcherIp, dispatcherPort);
            this.dispatcherProviders.add(dispatcherReferenceConfig);
        }
    }

    /// construct [ReferenceConfig]
    private ReferenceConfig<DispatcherService> constructReferenceConfig(
            final String dispatcherIp,
            final Integer dispatcherPort
    ) {
        // consume dispatcher service
        final ReferenceConfig<DispatcherService> consumer = new ReferenceConfig<>();
        consumer.setInterface(DispatcherService.class);
        // use tri:// protocol
        consumer.setProtocol(CommonConstants.TRIPLE);
        consumer.setProxy(CommonConstants.NATIVE_STUB);
        consumer.setRetries(3);
        consumer.setTimeout(10000);
        // need reset triple protocol here
        consumer.setUrl(CommonConstants.TRIPLE + "://"
                + dispatcherIp + ":" + (dispatcherPort + this.portOffset));
        return consumer;
    }
}
