package io.machinecode.chainlink.spi.management;

import io.machinecode.chainlink.spi.exception.NoConfigurationWithIdException;
import io.machinecode.chainlink.spi.inject.DependencyInjectionExtension;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Environment {

    void initialize(final List<DependencyInjectionExtension> extensions);

    List<ExtendedJobOperator> getJobOperators();

    ExtendedJobOperator getJobOperator(final String id) throws NoConfigurationWithIdException;
}
