package io.machinecode.chainlink.ehache.test.repository;

import io.machinecode.chainlink.repository.ehcache.EhCacheRepository;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import io.machinecode.chainlink.spi.configuration.factory.RepositoryFactory;
import io.machinecode.chainlink.spi.repository.Repository;
import io.machinecode.chainlink.core.repository.RepositoryTest;
import net.sf.ehcache.CacheManager;
import org.junit.After;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class EhCacheRepositoryTest extends RepositoryTest {

    private static final CacheManager manager = new CacheManager();

    @Override
    protected void visitJobOperatorModel(final JobOperatorModel model) throws Exception {
        model.getRepository().setFactory(new RepositoryFactory() {
            @Override
            public Repository produce(final Dependencies dependencies, final PropertyLookup properties) throws Exception {
                return _repository = new EhCacheRepository(
                        dependencies.getMarshalling(),
                        manager
                );
            }
        });
    }

    @After
    public void after() throws Exception {
        manager.getCache(EhCacheRepository.class.getCanonicalName() + ".ids").removeAll();
        manager.getCache(EhCacheRepository.class.getCanonicalName() + ".jobInstances").removeAll();
        manager.getCache(EhCacheRepository.class.getCanonicalName() + ".jobExecutions").removeAll();
        manager.getCache(EhCacheRepository.class.getCanonicalName() + ".stepExecutions").removeAll();
        manager.getCache(EhCacheRepository.class.getCanonicalName() + ".partitionExecutions").removeAll();
        manager.getCache(EhCacheRepository.class.getCanonicalName() + ".jobInstanceExecutions").removeAll();
        manager.getCache(EhCacheRepository.class.getCanonicalName() + ".jobExecutionInstances").removeAll();
        manager.getCache(EhCacheRepository.class.getCanonicalName() + ".jobExecutionStepExecutions").removeAll();
        manager.getCache(EhCacheRepository.class.getCanonicalName() + ".latestJobExecutionForInstance").removeAll();
        manager.getCache(EhCacheRepository.class.getCanonicalName() + ".stepExecutionPartitionExecutions").removeAll();
        manager.getCache(EhCacheRepository.class.getCanonicalName() + ".jobExecutionHistory").removeAll();
    }
}
