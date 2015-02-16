package io.machinecode.chainlink.spi.context;

import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface ExecutionContext extends Serializable {

    Long getRestartJobExecutionId();

    boolean isRestarting();

    long getJobExecutionId();

    Long getStepExecutionId();

    Long getPartitionExecutionId();

    JobContext getJobContext();

    StepContext getStepContext();

    Item[] getItems();

    String getRestartElementId();

    Long getLastStepExecutionId();

    long[] getPriorStepExecutionIds();
}
