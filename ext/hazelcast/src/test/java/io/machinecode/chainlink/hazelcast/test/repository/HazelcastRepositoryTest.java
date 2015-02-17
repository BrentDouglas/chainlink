package io.machinecode.chainlink.hazelcast.test.repository;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import io.machinecode.chainlink.repository.hazelcast.HazelcastRepository;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.factory.RepositoryFactory;
import io.machinecode.chainlink.spi.repository.Repository;
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
        model.getRepository().setFactory(new RepositoryFactory() {
            @Override
            public Repository produce(final Dependencies dependencies, final Properties properties) throws Exception {
                return _repository = new HazelcastRepository(
                        dependencies.getMarshalling(),
                        hazelcast
                );
            }
        });
    }

    @After
    public void after() throws Exception {
        hazelcast.getMap(HazelcastRepository.class.getCanonicalName() + ".ids").clear();
        hazelcast.getMap(HazelcastRepository.class.getCanonicalName() + ".jobInstances").clear();
        hazelcast.getMap(HazelcastRepository.class.getCanonicalName() + ".jobExecutions").clear();
        hazelcast.getMap(HazelcastRepository.class.getCanonicalName() + ".stepExecutions").clear();
        hazelcast.getMap(HazelcastRepository.class.getCanonicalName() + ".partitionExecutions").clear();
        hazelcast.getMap(HazelcastRepository.class.getCanonicalName() + ".jobInstanceExecutions").clear();
        hazelcast.getMap(HazelcastRepository.class.getCanonicalName() + ".jobExecutionInstances").clear();
        hazelcast.getMap(HazelcastRepository.class.getCanonicalName() + ".jobExecutionStepExecutions").clear();
        hazelcast.getMap(HazelcastRepository.class.getCanonicalName() + ".latestJobExecutionForInstance").clear();
        hazelcast.getMap(HazelcastRepository.class.getCanonicalName() + ".stepExecutionPartitionExecutions").clear();
        hazelcast.getMap(HazelcastRepository.class.getCanonicalName() + ".jobExecutionHistory").clear();
    }
}
