package io.machinecode.chainlink.spi.configuration;

import io.machinecode.chainlink.spi.transport.Transport;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface ExecutorConfiguration extends TransportConfiguration {

    Transport<?> getTransport();
}
