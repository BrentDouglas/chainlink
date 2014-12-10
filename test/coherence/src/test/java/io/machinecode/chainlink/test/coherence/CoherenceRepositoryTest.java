package io.machinecode.chainlink.test.coherence;

import com.tangosol.net.CacheFactory;
import io.machinecode.chainlink.repository.coherence.CoherenceExecutonRepository;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.configuration.factory.ExecutionRepositoryFactory;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.test.core.execution.RepositoryTest;

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
}
