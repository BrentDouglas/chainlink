package io.machinecode.chainlink.test;

import io.machinecode.chainlink.repository.memory.MemoryExecutionRepository;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.test.core.execution.RepositoryTest;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class MemoryRepositoryTest extends RepositoryTest {

    @Override
    protected ExecutionRepository _repository() throws Exception {
        return new MemoryExecutionRepository(marshallerFactory().produce(null));
    }
}
