package io.machinecode.chainlink.test.coherence;

import com.tangosol.net.CacheFactory;
import io.machinecode.chainlink.repository.coherence.CoherenceExecutonRepository;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.configuration.factory.ExecutionRepositoryFactory;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.core.repository.RepositoryTest;
import org.junit.After;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class CoherenceRepositoryTest extends RepositoryTest {

    @Override
    protected void visitJobOperatorModel(final JobOperatorModel model) throws Exception {
        CacheFactory.ensureCluster();
        model.getExecutionRepository().setFactory(new ExecutionRepositoryFactory() {
            @Override
            public ExecutionRepository produce(final Dependencies dependencies, final Properties properties) throws Exception {
                return _repository = new CoherenceExecutonRepository(
                        dependencies.getMarshalling()
                );
            }
        });
    }

    @After
    public void after() throws Exception {
        CacheFactory.getCache(CoherenceExecutonRepository.class.getCanonicalName() + ".ids").clear();
        CacheFactory.getCache(CoherenceExecutonRepository.class.getCanonicalName() + ".jobInstances").clear();
        CacheFactory.getCache(CoherenceExecutonRepository.class.getCanonicalName() + ".jobExecutions").clear();
        CacheFactory.getCache(CoherenceExecutonRepository.class.getCanonicalName() + ".stepExecutions").clear();
        CacheFactory.getCache(CoherenceExecutonRepository.class.getCanonicalName() + ".partitionExecutions").clear();
        CacheFactory.getCache(CoherenceExecutonRepository.class.getCanonicalName() + ".jobInstanceExecutions").clear();
        CacheFactory.getCache(CoherenceExecutonRepository.class.getCanonicalName() + ".jobExecutionInstances").clear();
        CacheFactory.getCache(CoherenceExecutonRepository.class.getCanonicalName() + ".jobExecutionStepExecutions").clear();
        CacheFactory.getCache(CoherenceExecutonRepository.class.getCanonicalName() + ".latestJobExecutionForInstance").clear();
        CacheFactory.getCache(CoherenceExecutonRepository.class.getCanonicalName() + ".stepExecutionPartitionExecutions").clear();
        CacheFactory.getCache(CoherenceExecutonRepository.class.getCanonicalName() + ".jobExecutionHistory").clear();
    }
}
