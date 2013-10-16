package io.machinecode.nock.spi.transport;

import io.machinecode.nock.spi.configuration.RuntimeConfiguration;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface TransportFactory {

    Transport produce(RuntimeConfiguration configuration, int threads);
}
