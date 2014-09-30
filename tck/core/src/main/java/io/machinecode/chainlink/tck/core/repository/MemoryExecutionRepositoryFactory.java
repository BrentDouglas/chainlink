package io.machinecode.chainlink.tck.core.repository;

import io.machinecode.chainlink.repository.memory.MemoryExecutionRepository;
import io.machinecode.chainlink.spi.configuration.RepositoryConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.ExecutionRepositoryFactory;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class MemoryExecutionRepositoryFactory implements ExecutionRepositoryFactory {
    @Override
    public ExecutionRepository produce(final RepositoryConfiguration configuration) throws Exception {
        return new MemoryExecutionRepository(configuration.getMarshallerFactory().produce(configuration));
    }
}
