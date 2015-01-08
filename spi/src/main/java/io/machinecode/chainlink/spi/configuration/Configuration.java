package io.machinecode.chainlink.spi.configuration;

import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Configuration extends Dependencies {

    String getProperty(final String name);

    String getProperty(final String name, final String defaultValue);

    Executor getExecutor();

    ExecutionRepository getExecutionRepository(final ExecutionRepositoryId id);
}
