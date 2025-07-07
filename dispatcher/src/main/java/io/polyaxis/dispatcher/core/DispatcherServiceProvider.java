package io.polyaxis.dispatcher.core;

import io.polyaxis.network.dispatcher.BrokerRefreshReq;
import io.polyaxis.network.dispatcher.BrokerRefreshResp;
import io.polyaxis.network.dispatcher.BrokerRegisterReq;
import io.polyaxis.network.dispatcher.BrokerRegisterResp;
import io.polyaxis.network.dispatcher.DubboDispatcherServiceTriple;

import java.util.concurrent.CompletableFuture;

/// Application dispatcher service provider.
///
/// @author github.com/MoritzArena
/// @date 2025/07/06
/// @since 1.0
public class DispatcherServiceProvider
        extends DubboDispatcherServiceTriple.DispatcherServiceImplBase {

    @Override
    public CompletableFuture<BrokerRegisterResp> registerBrokerAsync(BrokerRegisterReq request) {
        return super.registerBrokerAsync(request);
    }

    @Override
    public CompletableFuture<BrokerRefreshResp> refreshBrokerAsync(BrokerRefreshReq request) {
        return super.refreshBrokerAsync(request);
    }
}
