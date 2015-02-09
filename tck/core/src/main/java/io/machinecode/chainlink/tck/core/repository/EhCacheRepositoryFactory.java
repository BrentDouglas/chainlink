package io.machinecode.chainlink.tck.core.repository;

import io.machinecode.chainlink.repository.ehcache.EhCacheRepository;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.factory.RepositoryFactory;
import io.machinecode.chainlink.spi.repository.Repository;
import net.sf.ehcache.CacheManager;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class EhCacheRepositoryFactory implements RepositoryFactory {
    @Override
    public Repository produce(final Dependencies dependencies, final Properties properties) throws Exception {
        return new EhCacheRepository(
                dependencies.getMarshalling(),
                new CacheManager()
        );
    }
}
