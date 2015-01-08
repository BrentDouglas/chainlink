package io.machinecode.chainlink.spi;

import io.machinecode.chainlink.spi.configuration.Configuration;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Lifecycle extends AutoCloseable {

    void open(final Configuration configuration) throws Exception;
}
