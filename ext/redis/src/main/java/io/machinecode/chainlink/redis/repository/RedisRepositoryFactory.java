package io.machinecode.chainlink.redis.repository;

import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.factory.RepositoryFactory;
import io.machinecode.chainlink.spi.repository.Repository;
import redis.clients.jedis.JedisShardInfo;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class RedisRepositoryFactory implements RepositoryFactory {
    @Override
    public Repository produce(final Dependencies dependencies, final Properties properties) throws Exception {
        return new RedisRepository(
                new JedisShardInfo(
                        properties.getProperty("io.machinecode.chainlink.redis.host"),
                        Integer.parseInt(properties.getProperty("io.machinecode.chainlink.redis.port"))
                ),
                dependencies.getClassLoader(),
                dependencies.getMarshalling()
        );
    }
}
