package io.polyaxis.dispatcher.core;

import io.polyaxis.network.dispatcher.BrokerRefreshReq;
import io.polyaxis.network.dispatcher.BrokerRefreshResp;
import io.polyaxis.network.dispatcher.BrokerRegisterReq;
import io.polyaxis.network.dispatcher.BrokerRegisterResp;
import io.polyaxis.network.dispatcher.DubboDispatcherServiceTriple;
import io.smallrye.mutiny.Uni;

/// Application dispatcher service provider.
///
/// @author github.com/MoritzArena
/// @date 2025/07/06
/// @since 1.0
public class DispatcherServiceProvider
        extends DubboDispatcherServiceTriple.DispatcherServiceImplBase {

    @Override
    public Uni<BrokerRegisterResp> registerBroker(Uni<BrokerRegisterReq> request) {
        return Uni.createFrom().item(BrokerRegisterResp.newBuilder().build());
    }

    @Override
    public Uni<BrokerRefreshResp> refreshBroker(Uni<BrokerRefreshReq> request) {
        return Uni.createFrom().item(BrokerRefreshResp.newBuilder().build());
    }
}
