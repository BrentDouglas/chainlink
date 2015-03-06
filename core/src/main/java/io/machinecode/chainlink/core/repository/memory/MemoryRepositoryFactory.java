package io.machinecode.chainlink.core.repository.memory;

import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import io.machinecode.chainlink.spi.configuration.factory.RepositoryFactory;
import io.machinecode.chainlink.spi.repository.Repository;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class MemoryRepositoryFactory implements RepositoryFactory {
    @Override
    public Repository produce(final Dependencies dependencies, final PropertyLookup properties) throws Exception {
        return new MemoryRepository(dependencies.getMarshalling());
    }
}
