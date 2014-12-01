package io.machinecode.chainlink.test;

import io.machinecode.chainlink.repository.redis.RedisExecutionRepository;
import io.machinecode.chainlink.spi.configuration.RepositoryConfiguration;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.test.core.execution.RepositoryTest;
import redis.clients.jedis.JedisShardInfo;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class RedisRepositoryTest extends RepositoryTest {

    @Override
    protected ExecutionRepository _repository() throws Exception {
        return new RedisExecutionRepository(
                new JedisShardInfo(
                        System.getProperty("redis.host"),
                        Integer.parseInt(System.getProperty("redis.port"))
                ),
                Thread.currentThread().getContextClassLoader(),
                marshallerFactory().produce(null)
        );
    }
}
