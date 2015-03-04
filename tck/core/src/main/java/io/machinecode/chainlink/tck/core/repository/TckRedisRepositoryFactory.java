package io.machinecode.chainlink.tck.core.repository;

import io.machinecode.chainlink.redis.repository.RedisRepository;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.factory.RepositoryFactory;
import io.machinecode.chainlink.spi.repository.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class TckRedisRepositoryFactory implements RepositoryFactory {

    static {
        final Jedis jedis = new JedisShardInfo(
                System.getProperty("redis.host"),
                Integer.parseInt(System.getProperty("redis.port"))
        ).createResource();
        try {
            jedis.flushDB();
        } finally {
            jedis.disconnect();
        }
    }

    @Override
    public Repository produce(final Dependencies dependencies, final Properties properties) throws Exception {
        return new RedisRepository(
                new JedisShardInfo(
                        System.getProperty("redis.host"),
                        Integer.parseInt(System.getProperty("redis.port"))
                ),
                dependencies.getClassLoader(),
                dependencies.getMarshalling()
        );
    }
}
