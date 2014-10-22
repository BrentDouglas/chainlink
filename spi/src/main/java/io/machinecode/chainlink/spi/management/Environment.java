package io.machinecode.chainlink.spi.management;

import io.machinecode.chainlink.spi.exception.NoConfigurationWithIdException;

import java.util.Map;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Environment {

    ExtendedJobOperator getJobOperator(final String id) throws NoConfigurationWithIdException;

    Map<String, ? extends ExtendedJobOperator> getJobOperators();
}
