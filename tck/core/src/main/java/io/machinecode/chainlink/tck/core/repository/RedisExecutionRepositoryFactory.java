package io.machinecode.chainlink.tck.core.repository;

import io.machinecode.chainlink.repository.redis.RedisExecutionRepository;
import io.machinecode.chainlink.spi.configuration.RepositoryConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.ExecutionRepositoryFactory;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import redis.clients.jedis.JedisShardInfo;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class RedisExecutionRepositoryFactory implements ExecutionRepositoryFactory {
    @Override
    public ExecutionRepository produce(final RepositoryConfiguration configuration) throws Exception {
        return new RedisExecutionRepository(
                new JedisShardInfo(
                        System.getProperty("redis.host"),
                        Integer.parseInt(System.getProperty("redis.port"))
                ),
                configuration.getMarshallerFactory().produce(configuration)
        );
    }
}
