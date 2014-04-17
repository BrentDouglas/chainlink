package io.machinecode.chainlink.spi.management;

import io.machinecode.chainlink.spi.configuration.Configuration;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Environment {

    List<ExtendedJobOperator> getJobOperators();

    ExtendedJobOperator getJobOperator(final Configuration configuration);
}
