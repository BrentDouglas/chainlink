package io.machinecode.chainlink.test;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import io.machinecode.chainlink.repository.hazelcast.HazelcastExecutonRepository;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.test.core.execution.RepositoryTest;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class HazelcastRepositoryTest extends RepositoryTest {

    private static final HazelcastInstance hazelcast = Hazelcast.newHazelcastInstance();

    @Override
    protected ExecutionRepository _repository() throws Exception {
        return new HazelcastExecutonRepository(
                marshallerFactory().produce(null),
                hazelcast
        );
    }
}
