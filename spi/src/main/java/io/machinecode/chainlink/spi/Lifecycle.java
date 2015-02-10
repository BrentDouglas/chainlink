package io.machinecode.chainlink.spi;

import io.machinecode.chainlink.spi.configuration.Configuration;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Lifecycle extends AutoCloseable {

    /**
     * <p>Called after the entire configuration has been resolved.</p>
     *
     * @param configuration The configuration.
     * @throws Exception On any implementation specific errors.
     */
    void open(final Configuration configuration) throws Exception;
}
