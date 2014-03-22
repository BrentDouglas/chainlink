package io.machinecode.chainlink.tck.cdi;

import io.machinecode.chainlink.core.configuration.ConfigurationImpl.Builder;
import io.machinecode.chainlink.core.transaction.LocalTransactionManager;
import io.machinecode.chainlink.inject.cdi.CdiArtifactLoader;
import io.machinecode.chainlink.inject.cdi.CdiInjector;
import io.machinecode.chainlink.repository.redis.RedisExecutionRepository;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.ConfigurationFactory;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import redis.clients.jedis.JedisShardInfo;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class RedisCdiConfigurationFactory implements ConfigurationFactory {

    private static Weld weld;
    private static WeldContainer container;

    static {
        weld = new Weld();
        container = weld.initialize();
    }

    @Override
    public Configuration produce() throws IOException {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        return new Builder()
                .setClassLoader(tccl)
                .setExecutionRepository(new RedisExecutionRepository(
                        tccl,
                        new JedisShardInfo(
                                System.getProperty("redis.host"),
                                Integer.parseInt(System.getProperty("redis.port"))
                        )
                ))
                .setTransactionManager(new LocalTransactionManager(180, TimeUnit.SECONDS))
                .setArtifactLoaders(CdiArtifactLoader.inject(container.getBeanManager(), CdiArtifactLoader.class))
                .setInjectors(CdiArtifactLoader.inject(container.getBeanManager(), CdiInjector.class))
                .build();
    }
}
