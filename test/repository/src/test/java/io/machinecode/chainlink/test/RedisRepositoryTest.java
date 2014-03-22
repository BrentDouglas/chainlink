package io.machinecode.chainlink.test;

import io.machinecode.chainlink.repository.redis.RedisExecutionRepository;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.test.core.execution.RepositoryTest;
import redis.clients.jedis.JedisShardInfo;

import java.io.IOException;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class RedisRepositoryTest extends RepositoryTest {

    @Override
    protected ExecutionRepository _repository() throws IOException {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        return new RedisExecutionRepository(
                tccl,
                new JedisShardInfo(
                        System.getProperty("redis.host"),
                        Integer.parseInt(System.getProperty("redis.port"))
                )
        );
    }
}
