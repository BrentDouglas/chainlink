package io.machinecode.chainlink.spi.inject;

import io.machinecode.chainlink.spi.management.Environment;
import io.machinecode.chainlink.spi.management.ExtendedJobOperator;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface DependencyInjectionExtension {

    void register(final Environment environment);

    void register(final String id, final ExtendedJobOperator operator);
}
