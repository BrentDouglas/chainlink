package io.machinecode.chainlink.repository.coherence;

import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import io.machinecode.chainlink.spi.configuration.factory.RepositoryFactory;
import io.machinecode.chainlink.spi.repository.Repository;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class CoherenceRepositoryFactory implements RepositoryFactory {
    @Override
    public Repository produce(final Dependencies dependencies, final PropertyLookup properties) throws Exception {
        return new CoherenceRepository(
                dependencies.getMarshalling()
        );
    }
}
