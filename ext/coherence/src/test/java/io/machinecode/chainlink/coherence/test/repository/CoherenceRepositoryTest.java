package io.machinecode.chainlink.coherence.test.repository;

import com.tangosol.net.CacheFactory;
import io.machinecode.chainlink.repository.coherence.CoherenceRepository;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.configuration.factory.RepositoryFactory;
import io.machinecode.chainlink.spi.repository.Repository;
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
        model.getRepository().setFactory(new RepositoryFactory() {
            @Override
            public Repository produce(final Dependencies dependencies, final Properties properties) throws Exception {
                return _repository = new CoherenceRepository(
                        dependencies.getMarshalling()
                );
            }
        });
    }

    @After
    public void after() throws Exception {
        CacheFactory.getCache(CoherenceRepository.class.getCanonicalName() + ".ids").clear();
        CacheFactory.getCache(CoherenceRepository.class.getCanonicalName() + ".jobInstances").clear();
        CacheFactory.getCache(CoherenceRepository.class.getCanonicalName() + ".jobExecutions").clear();
        CacheFactory.getCache(CoherenceRepository.class.getCanonicalName() + ".stepExecutions").clear();
        CacheFactory.getCache(CoherenceRepository.class.getCanonicalName() + ".partitionExecutions").clear();
        CacheFactory.getCache(CoherenceRepository.class.getCanonicalName() + ".jobInstanceExecutions").clear();
        CacheFactory.getCache(CoherenceRepository.class.getCanonicalName() + ".jobExecutionInstances").clear();
        CacheFactory.getCache(CoherenceRepository.class.getCanonicalName() + ".jobExecutionStepExecutions").clear();
        CacheFactory.getCache(CoherenceRepository.class.getCanonicalName() + ".latestJobExecutionForInstance").clear();
        CacheFactory.getCache(CoherenceRepository.class.getCanonicalName() + ".stepExecutionPartitionExecutions").clear();
        CacheFactory.getCache(CoherenceRepository.class.getCanonicalName() + ".jobExecutionHistory").clear();
    }
}
