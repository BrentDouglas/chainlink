package io.machinecode.chainlink.tck.core.repository;

import io.machinecode.chainlink.repository.ehcache.EhCacheExecutionRepository;
import io.machinecode.chainlink.spi.configuration.ExecutionRepositoryConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.ExecutionRepositoryFactory;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import net.sf.ehcache.CacheManager;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class EhCacheExecutionRepositoryFactory implements ExecutionRepositoryFactory {
    @Override
    public ExecutionRepository produce(final ExecutionRepositoryConfiguration configuration) throws Exception {
        return new EhCacheExecutionRepository(
                configuration.getMarshallingProviderFactory().produce(configuration),
                new CacheManager()
        );
    }
}
