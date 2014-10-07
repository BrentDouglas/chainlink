package io.machinecode.chainlink.spi.repository;

import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface PartitionExecutionBuilder<T extends PartitionExecutionBuilder<T>> extends BaseExecutionBuilder<T> {

    T setPartitionExecutionId(final long partitionExecutionId);

    T setStepExecutionId(final long stepExecutionId);

    T setPartitionId(final int partitionId);

    T setPartitionParameters(final Properties partitionParameters);

    PartitionExecution build();
}
