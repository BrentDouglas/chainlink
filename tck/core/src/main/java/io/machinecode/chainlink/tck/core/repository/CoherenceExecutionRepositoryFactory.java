package io.machinecode.chainlink.tck.core.repository;

import io.machinecode.chainlink.repository.coherence.CoherenceExecutonRepository;
import io.machinecode.chainlink.spi.configuration.RepositoryConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.ExecutionRepositoryFactory;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class CoherenceExecutionRepositoryFactory implements ExecutionRepositoryFactory {
    @Override
    public ExecutionRepository produce(final RepositoryConfiguration configuration) throws Exception {
        return new CoherenceExecutonRepository(
                configuration.getMarshallingProviderFactory().produce(configuration)
        );
    }
}
