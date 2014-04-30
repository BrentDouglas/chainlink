package io.machinecode.chainlink.spi.management;

import io.machinecode.chainlink.spi.exception.NoConfigurationWithIdException;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Environment {

    List<ExtendedJobOperator> getJobOperators();

    ExtendedJobOperator getJobOperator(final String id) throws NoConfigurationWithIdException;
}
