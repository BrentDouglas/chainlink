package io.machinecode.chainlink.test;

import io.machinecode.chainlink.repository.ehcache.EhCacheExecutionRepository;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.factory.ExecutionRepositoryFactory;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.core.repository.RepositoryTest;
import net.sf.ehcache.CacheManager;
import org.junit.After;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class EhCacheRepositoryTest extends RepositoryTest {

    private static final CacheManager manager = new CacheManager();

    @Override
    protected void visitJobOperatorModel(final JobOperatorModel model) throws Exception {
        model.getExecutionRepository().setFactory(new ExecutionRepositoryFactory() {
            @Override
            public ExecutionRepository produce(final Dependencies dependencies, final Properties properties) throws Exception {
                return _repository = new EhCacheExecutionRepository(
                        dependencies.getMarshalling(),
                        manager
                );
            }
        });
    }

    @After
    public void after() throws Exception {
        manager.getCache(EhCacheExecutionRepository.class.getCanonicalName() + ".ids").removeAll();
        manager.getCache(EhCacheExecutionRepository.class.getCanonicalName() + ".jobInstances").removeAll();
        manager.getCache(EhCacheExecutionRepository.class.getCanonicalName() + ".jobExecutions").removeAll();
        manager.getCache(EhCacheExecutionRepository.class.getCanonicalName() + ".stepExecutions").removeAll();
        manager.getCache(EhCacheExecutionRepository.class.getCanonicalName() + ".partitionExecutions").removeAll();
        manager.getCache(EhCacheExecutionRepository.class.getCanonicalName() + ".jobInstanceExecutions").removeAll();
        manager.getCache(EhCacheExecutionRepository.class.getCanonicalName() + ".jobExecutionInstances").removeAll();
        manager.getCache(EhCacheExecutionRepository.class.getCanonicalName() + ".jobExecutionStepExecutions").removeAll();
        manager.getCache(EhCacheExecutionRepository.class.getCanonicalName() + ".latestJobExecutionForInstance").removeAll();
        manager.getCache(EhCacheExecutionRepository.class.getCanonicalName() + ".stepExecutionPartitionExecutions").removeAll();
        manager.getCache(EhCacheExecutionRepository.class.getCanonicalName() + ".jobExecutionHistory").removeAll();
    }
}
