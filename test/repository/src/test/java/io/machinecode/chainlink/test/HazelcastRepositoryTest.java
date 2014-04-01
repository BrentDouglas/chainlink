package io.machinecode.chainlink.test;

import com.hazelcast.core.Hazelcast;
import io.machinecode.chainlink.repository.core.JdkSerializer;
import io.machinecode.chainlink.repository.hazelcast.HazelcastExecutonRepository;
import io.machinecode.chainlink.repository.infinispan.InfinispanExecutionRepository;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.test.core.execution.RepositoryTest;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.transaction.LockingMode;
import org.infinispan.transaction.TransactionMode;
import org.infinispan.transaction.lookup.TransactionManagerLookup;
import org.infinispan.util.concurrent.IsolationLevel;

import javax.transaction.TransactionManager;
import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class HazelcastRepositoryTest extends RepositoryTest {

    @Override
    protected ExecutionRepository _repository() {
        return new HazelcastExecutonRepository(
                new JdkSerializer(),
                Hazelcast.newHazelcastInstance()
        );
    }
}
