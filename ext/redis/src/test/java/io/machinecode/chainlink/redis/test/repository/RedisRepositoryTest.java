package io.machinecode.chainlink.redis.test.repository;

import io.machinecode.chainlink.core.repository.MutableMetricImpl;
import io.machinecode.chainlink.repository.redis.RedisRepository;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.factory.RepositoryFactory;
import io.machinecode.chainlink.spi.repository.Repository;
import io.machinecode.chainlink.core.repository.RepositoryTest;
import org.junit.After;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
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
    protected void visitJobOperatorModel(final JobOperatorModel model) throws Exception {
        model.getRepository().setFactory(new RepositoryFactory() {
            @Override
            public Repository produce(final Dependencies dependencies, final Properties properties) throws Exception {
                return _repository = new RedisRepository(
                        _info(),
                        MutableMetricImpl.class.getClassLoader(),
                        dependencies.getMarshalling()
                );
            }
        });
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
