package io.machinecode.chainlink.spi.context;

import javax.batch.runtime.BatchStatus;
import java.io.Serializable;

/**
 * <p>The outcome of the execution of a single {@link io.machinecode.chainlink.spi.jsl.task.Task}
 *  (a batchlet or chunk invocation on a single thread).</p>
 *
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Item {

    /**
     * @return The result of {@link javax.batch.api.partition.PartitionCollector#collectPartitionData()}
     *         or {@code null} if no collector was specified.
     */
    Serializable getData();

    /**
     * @return The result of {@link javax.batch.runtime.context.StepContext#getBatchStatus()} after
     *         the task completed.
     */
    BatchStatus getBatchStatus();

    /**
     * @return The result of {@link javax.batch.runtime.context.StepContext#getExitStatus()} after
     *         the task completed.
     */
    String getExitStatus();
}
