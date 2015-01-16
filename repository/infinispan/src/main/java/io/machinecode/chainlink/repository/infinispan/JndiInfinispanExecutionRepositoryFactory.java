package io.machinecode.chainlink.repository.infinispan;

import io.machinecode.chainlink.spi.Constants;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.factory.ExecutionRepositoryFactory;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import org.infinispan.manager.EmbeddedCacheManager;

import javax.naming.InitialContext;
import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JndiInfinispanExecutionRepositoryFactory implements ExecutionRepositoryFactory {
    @Override
    public ExecutionRepository produce(final Dependencies dependencies, final Properties properties) throws Exception {
        return new InfinispanExecutionRepository(
                dependencies.getMarshalling(),
                InitialContext.<EmbeddedCacheManager>doLookup(properties.getProperty(Constants.CACHE_MANAGER_JNDI_NAME))
        );
    }
}
