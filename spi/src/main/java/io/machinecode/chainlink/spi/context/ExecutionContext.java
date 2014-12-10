package io.machinecode.chainlink.spi.context;

import io.machinecode.chainlink.spi.work.JobWork;

import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface ExecutionContext extends Serializable {

    JobWork getJob();

    Long getRestartJobExecutionId();

    boolean isRestarting();

    long getJobExecutionId();

    Long getStepExecutionId();

    Long getPartitionExecutionId();

    MutableJobContext getJobContext();

    MutableStepContext getStepContext();

    void setStepContext(final MutableStepContext stepContext);

    Item[] getItems();

    void setItems(final Item... items);

    String getRestartElementId();

    void setRestartElementId(final String restartElementId);

    Long getLastStepExecutionId();

    void setLastStepExecutionId(final long lastStepExecutionId);

    long[] getPriorStepExecutionIds();

    void setPriorStepExecutionId(final long[] priorStepExecutionId);
}
