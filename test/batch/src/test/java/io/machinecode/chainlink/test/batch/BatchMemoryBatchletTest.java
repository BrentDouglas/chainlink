package io.machinecode.chainlink.test.batch;

import io.machinecode.chainlink.marshalling.jdk.JdkMarshallingProvider;
import io.machinecode.chainlink.repository.memory.MemoryExecutionRepository;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.test.core.execution.batchlet.BatchletTest;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class BatchMemoryBatchletTest extends BatchletTest {
    @Override
    protected ExecutionRepository _repository() {
        return new MemoryExecutionRepository(new JdkMarshallingProvider());
    }
}
