package io.machinecode.chainlink.test;

import io.machinecode.chainlink.repository.core.MutableMetricImpl;
import io.machinecode.chainlink.repository.redis.RedisExecutionRepository;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.test.core.execution.RepositoryTest;
import org.junit.After;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class RedisRepositoryTest extends RepositoryTest {

    protected JedisShardInfo info;

    private JedisShardInfo _info() {
        if (info == null) {
            info = new JedisShardInfo(
                    System.getProperty("redis.host"),
                    Integer.parseInt(System.getProperty("redis.port"))
            );
        }
        return info;
    }

    @Override
    protected ExecutionRepository _repository() throws Exception {
        return new RedisExecutionRepository(
                _info(),
                MutableMetricImpl.class.getClassLoader(),
                marshallingProviderFactory().produce(null)
        );
    }

    @After
    public void after() throws Exception {
        final Jedis jedis = _info().createResource();
        try {
            jedis.flushDB();
        } finally {
            jedis.disconnect();
        }
    }
}
