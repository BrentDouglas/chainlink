package io.machinecode.chainlink.spi.context;

import io.machinecode.chainlink.spi.ExtendedJobExecution;
import io.machinecode.chainlink.spi.execution.Item;
import io.machinecode.chainlink.spi.work.JobWork;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ExecutionContext extends Serializable {

    JobWork getJob();

    ExtendedJobExecution getJobExecution();

    ExtendedJobExecution getRestartJobExecution();

    boolean isRestarting();

    long getJobExecutionId();

    Long getStepExecutionId();

    Integer getPartitionId();

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

    void addPriorStepExecutionId(final long priorStepExecutionId);
}
