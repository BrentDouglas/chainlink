package io.machinecode.chainlink.spi.repository;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface PartitionExecutionBuilder<T extends PartitionExecutionBuilder<T>> extends BaseExecutionBuilder<T> {

    T setPartitionExecutionId(final long partitionExecutionId);

    T setStepExecutionId(final long stepExecutionId);

    T setPartitionId(final int partitionId);

    T setPartitionParameters(final Properties partitionParameters);

    PartitionExecution build();
}
