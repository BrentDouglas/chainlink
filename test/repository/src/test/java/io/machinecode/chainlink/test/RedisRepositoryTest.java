package io.machinecode.chainlink.test;

import io.machinecode.chainlink.repository.redis.RedisExecutionRepository;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.test.core.execution.RepositoryTest;
import io.mashinecode.chainlink.marshalling.jdk.JdkMarshaller;
import redis.clients.jedis.JedisShardInfo;

import java.io.IOException;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class RedisRepositoryTest extends RepositoryTest {

    @Override
    protected ExecutionRepository _repository() throws IOException {
        return new RedisExecutionRepository(
                new JedisShardInfo(
                        System.getProperty("redis.host"),
                        Integer.parseInt(System.getProperty("redis.port"))
                ),
                new JdkMarshaller()
        );
    }
}
