package io.machinecode.chainlink.test;

import io.machinecode.chainlink.repository.memory.MemoryExecutionRepository;
import io.machinecode.chainlink.spi.ExecutionRepository;
import io.machinecode.chainlink.test.core.execution.RepositoryTest;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class MemoryRepositoryTest extends RepositoryTest {

    @Override
    protected ExecutionRepository _repository() {
        return new MemoryExecutionRepository();
    }
}
