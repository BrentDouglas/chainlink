package io.machinecode.chainlink.test.batch;

import io.machinecode.chainlink.marshalling.jdk.JdkMarshaller;
import io.machinecode.chainlink.repository.memory.MemoryExecutionRepository;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.test.core.execution.BatchletTest;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class BatchMemoryBatchletTest extends BatchletTest {
    @Override
    protected ExecutionRepository _repository() {
        return new MemoryExecutionRepository(new JdkMarshaller());
    }
}
