package io.ployaxis.broker.core;

import io.polyaxis.network.broker.BrokerInvokeReq;
import io.polyaxis.network.broker.BrokerInvokeResp;
import io.polyaxis.network.broker.BrokerPluginSettingReq;
import io.polyaxis.network.broker.BrokerPluginSettingResp;
import io.polyaxis.network.broker.DubboBrokerServiceTriple;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

/// Broker Service Provider.
///
/// @author github.com/MoritzArena
/// @date 2025/07/08
/// @since 1.0
public class BrokerServiceProvider
        extends DubboBrokerServiceTriple.BrokerServiceImplBase {

    @Override
    public Uni<BrokerPluginSettingResp> setPlugin(Uni<BrokerPluginSettingReq> request) {
        return super.setPlugin(request);
    }

    @Override
    public Multi<BrokerInvokeResp> invokeCapability(Uni<BrokerInvokeReq> request) {
        return super.invokeCapability(request);
    }
}
