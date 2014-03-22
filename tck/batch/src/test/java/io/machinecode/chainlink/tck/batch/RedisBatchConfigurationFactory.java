package io.machinecode.chainlink.tck.batch;

import io.machinecode.chainlink.core.configuration.ConfigurationImpl.Builder;
import io.machinecode.chainlink.core.transaction.LocalTransactionManager;
import io.machinecode.chainlink.repository.redis.RedisExecutionRepository;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.ConfigurationFactory;
import redis.clients.jedis.JedisShardInfo;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class RedisBatchConfigurationFactory implements ConfigurationFactory {

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
                .build();
    }
}
