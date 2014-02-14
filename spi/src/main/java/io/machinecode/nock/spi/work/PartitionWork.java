package io.machinecode.nock.spi.work;

import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.element.partition.Partition;
import io.machinecode.nock.spi.element.partition.Strategy;
import io.machinecode.nock.spi.execution.Executable;
import io.machinecode.nock.spi.execution.Executor;
import io.machinecode.nock.spi.execution.Item;

import javax.batch.api.partition.PartitionReducer;
import javax.batch.runtime.BatchStatus;
import javax.transaction.TransactionManager;
import java.io.Serializable;
import java.util.List;

import static javax.batch.api.partition.PartitionReducer.PartitionStatus;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface PartitionWork<T extends Strategy> extends Partition<T>, Serializable {

    PartitionTarget map(final TaskWork task, final Executor executor, final Executable callback, final ExecutionContext context, final int timeout, final Long restartStepExecutionId) throws Exception;

    Item collect(final TaskWork task, final Executor executor, final ExecutionContext context, final BatchStatus batchStatus, final String exitStatus) throws Exception;

    PartitionStatus analyse(final Executor executor, final ExecutionContext context, final TransactionManager transactionManager, final Item... items) throws Exception;

    void reduce(final PartitionStatus partitionStatus, final Executor executor, final ExecutionContext context, final TransactionManager transactionManager) throws Exception;
}
