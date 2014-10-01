package io.machinecode.chainlink.test;

import io.machinecode.chainlink.repository.redis.RedisExecutionRepository;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.test.core.execution.RepositoryTest;
import redis.clients.jedis.JedisShardInfo;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class RedisRepositoryTest extends RepositoryTest {

    @Override
    protected ExecutionRepository _repository() throws Exception {
        return new RedisExecutionRepository(
                new JedisShardInfo(
                        System.getProperty("redis.host"),
                        Integer.parseInt(System.getProperty("redis.port"))
                ),
                marshallerFactory().produce(null)
        );
    }
}
