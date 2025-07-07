package io.polyaxis.dispatcher.core;

import org.apache.dubbo.config.bootstrap.DubboBootstrap;

/// Application dispatcher dubbo client.
///
/// Dubbo consumer is a lazy starter when receiving broker register request.
///
/// @author github.com/MoritzArena
/// @date 2025/07/06
/// @since 1.0
final class DispatcherDubboClient {

    public DispatcherDubboClient(
            final DubboBootstrap bootstrap,
            final Integer portOffset
    ) {
    }
}
