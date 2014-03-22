package io.machinecode.chainlink.tck.seam;

import io.machinecode.chainlink.core.configuration.ConfigurationImpl.Builder;
import io.machinecode.chainlink.core.transaction.LocalTransactionManager;
import io.machinecode.chainlink.inject.core.VetoInjector;
import io.machinecode.chainlink.inject.seam.SeamArtifactLoader;
import io.machinecode.chainlink.repository.redis.RedisExecutionRepository;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.ConfigurationFactory;
import org.jboss.seam.contexts.ServletLifecycle;
import org.jboss.seam.init.Initialization;
import org.jboss.seam.mock.MockServletContext;
import redis.clients.jedis.JedisShardInfo;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class RedisSeamConfigurationFactory implements ConfigurationFactory {

    static {
        final ServletContext context = new MockServletContext();
        ServletLifecycle.beginApplication(context);
        new Initialization(context).create().init();
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
                .setArtifactLoaders(SeamArtifactLoader.inject("seamArtifactLoader", SeamArtifactLoader.class))
                .setInjectors(new VetoInjector())
                .build();
    }
}
