package io.machinecode.chainlink.spi;

import io.machinecode.chainlink.spi.configuration.SubSystemModel;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Lifecycle<T> {

    T configure(final SubSystemModel model);

    void start();

    void stop();
}
