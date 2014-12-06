package io.machinecode.chainlink.test.coherence;

import com.tangosol.net.CacheFactory;
import io.machinecode.chainlink.repository.coherence.CoherenceExecutonRepository;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.test.core.execution.RepositoryTest;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class CoherenceRepositoryTest extends RepositoryTest {

    @Override
    protected ExecutionRepository _repository() throws Exception {
        CacheFactory.ensureCluster();
        return new CoherenceExecutonRepository(
                marshallerFactory().produce(null)
        );
    }
}
