package io.machinecode.chainlink.test;

import com.tangosol.net.CacheFactory;
import io.machinecode.chainlink.repository.coherence.CoherenceExecutonRepository;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.test.core.execution.RepositoryTest;
import io.mashinecode.chainlink.marshalling.jdk.JdkMarshaller;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CoherenceRepositoryTest extends RepositoryTest {

    @Override
    protected ExecutionRepository _repository() {
        CacheFactory.ensureCluster();
        return new CoherenceExecutonRepository(
                new JdkMarshaller()
        );
    }
}
