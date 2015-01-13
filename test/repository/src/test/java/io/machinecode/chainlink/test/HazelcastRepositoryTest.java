package io.machinecode.chainlink.test;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import io.machinecode.chainlink.repository.hazelcast.HazelcastExecutonRepository;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.factory.ExecutionRepositoryFactory;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.core.repository.RepositoryTest;
import org.junit.After;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class HazelcastRepositoryTest extends RepositoryTest {

    private static final HazelcastInstance hazelcast = Hazelcast.newHazelcastInstance();

    @Override
    protected void visitJobOperatorModel(final JobOperatorModel model) throws Exception {
        model.getExecutionRepository().setFactory(new ExecutionRepositoryFactory() {
            @Override
            public ExecutionRepository produce(final Dependencies dependencies, final Properties properties) throws Exception {
                return _repository = new HazelcastExecutonRepository(
                        dependencies.getMarshalling(),
                        hazelcast
                );
            }
        });
    }

    @After
    public void after() throws Exception {
        hazelcast.getMap(HazelcastExecutonRepository.class.getCanonicalName() + ".ids").clear();
        hazelcast.getMap(HazelcastExecutonRepository.class.getCanonicalName() + ".jobInstances").clear();
        hazelcast.getMap(HazelcastExecutonRepository.class.getCanonicalName() + ".jobExecutions").clear();
        hazelcast.getMap(HazelcastExecutonRepository.class.getCanonicalName() + ".stepExecutions").clear();
        hazelcast.getMap(HazelcastExecutonRepository.class.getCanonicalName() + ".partitionExecutions").clear();
        hazelcast.getMap(HazelcastExecutonRepository.class.getCanonicalName() + ".jobInstanceExecutions").clear();
        hazelcast.getMap(HazelcastExecutonRepository.class.getCanonicalName() + ".jobExecutionInstances").clear();
        hazelcast.getMap(HazelcastExecutonRepository.class.getCanonicalName() + ".jobExecutionStepExecutions").clear();
        hazelcast.getMap(HazelcastExecutonRepository.class.getCanonicalName() + ".latestJobExecutionForInstance").clear();
        hazelcast.getMap(HazelcastExecutonRepository.class.getCanonicalName() + ".stepExecutionPartitionExecutions").clear();
        hazelcast.getMap(HazelcastExecutonRepository.class.getCanonicalName() + ".jobExecutionHistory").clear();
    }
}
