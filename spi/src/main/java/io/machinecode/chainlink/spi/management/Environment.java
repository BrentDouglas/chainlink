package io.machinecode.chainlink.spi.management;

import io.machinecode.chainlink.spi.exception.NoConfigurationWithIdException;

import java.util.Map;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface Environment {

    ExtendedJobOperator getJobOperator(final String name) throws NoConfigurationWithIdException;

    Map<String, ? extends ExtendedJobOperator> getJobOperators();
}
