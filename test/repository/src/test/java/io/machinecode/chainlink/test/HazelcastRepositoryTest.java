package io.machinecode.chainlink.test;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import io.machinecode.chainlink.repository.hazelcast.HazelcastExecutonRepository;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.test.core.execution.RepositoryTest;
import io.mashinecode.chainlink.marshalling.jdk.JdkMarshaller;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class HazelcastRepositoryTest extends RepositoryTest {

    private static final HazelcastInstance hazelcast = Hazelcast.newHazelcastInstance();

    @Override
    protected ExecutionRepository _repository() {
        return new HazelcastExecutonRepository(
                new JdkMarshaller(),
                hazelcast
        );
    }
}
