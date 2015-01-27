package io.machinecode.chainlink.core.transport;

import io.machinecode.chainlink.spi.configuration.factory.TransportFactory;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
public interface TestTransportFactory extends TransportFactory, AutoCloseable {}
