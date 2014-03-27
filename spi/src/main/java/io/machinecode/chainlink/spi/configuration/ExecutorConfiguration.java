package io.machinecode.chainlink.spi.configuration;

import io.machinecode.chainlink.spi.transport.Transport;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ExecutorConfiguration extends TransportConfiguration {

    Transport getTransport();
}
