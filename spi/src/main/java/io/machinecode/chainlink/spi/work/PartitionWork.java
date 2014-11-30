package io.machinecode.chainlink.spi.work;

import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.context.Item;
import io.machinecode.chainlink.spi.element.partition.Partition;
import io.machinecode.chainlink.spi.element.partition.Strategy;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;

import javax.batch.runtime.BatchStatus;
import java.io.Serializable;

import static javax.batch.api.partition.PartitionReducer.PartitionStatus;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface PartitionWork<T extends Strategy> extends Partition<T>, Serializable {

    PartitionTarget map(final RuntimeConfiguration configuration, final ExecutionRepositoryId executionRepositoryId,
                        final TaskWork task, final ExecutableId callbackId, final ExecutionContext context, final int timeout,
                        final Long restartStepExecutionId) throws Exception;

    Item collect(final RuntimeConfiguration configuration, final ExecutionContext context, final BatchStatus batchStatus, final String exitStatus) throws Exception;

    PartitionStatus analyse(final RuntimeConfiguration configuration, final ExecutionContext context, final Item... items) throws Exception;

    void reduce(final RuntimeConfiguration configuration, final PartitionStatus partitionStatus, final ExecutionContext context) throws Exception;
}
