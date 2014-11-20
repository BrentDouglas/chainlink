package io.machinecode.chainlink.tck.core.repository;

import io.machinecode.chainlink.repository.ehcache.EhCacheExecutionRepository;
import io.machinecode.chainlink.spi.configuration.RepositoryConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.ExecutionRepositoryFactory;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import net.sf.ehcache.CacheManager;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class EhCacheExecutionRepositoryFactory implements ExecutionRepositoryFactory {
    @Override
    public ExecutionRepository produce(final RepositoryConfiguration configuration) throws Exception {
        return new EhCacheExecutionRepository(
                configuration.getMarshallingProviderFactory().produce(configuration),
                new CacheManager()
        );
    }
}
