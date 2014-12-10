package io.machinecode.chainlink.tck.core.repository;

import io.machinecode.chainlink.repository.ehcache.EhCacheExecutionRepository;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.factory.ExecutionRepositoryFactory;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import net.sf.ehcache.CacheManager;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class EhCacheExecutionRepositoryFactory implements ExecutionRepositoryFactory {
    @Override
    public ExecutionRepository produce(final Dependencies dependencies, final Properties properties) throws Exception {
        return new EhCacheExecutionRepository(
                dependencies.getMarshalling(),
                new CacheManager()
        );
    }
}
