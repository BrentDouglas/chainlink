package io.machinecode.nock.spi.context;

import io.machinecode.nock.spi.RestartableJobExecution;
import io.machinecode.nock.spi.execution.Item;
import io.machinecode.nock.spi.work.JobWork;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ExecutionContext extends Serializable {

    JobWork getJob();

    RestartableJobExecution getJobExecution();

    long getJobInstanceId();

    long getJobExecutionId();

    Long getStepExecutionId();

    MutableJobContext getJobContext();

    MutableStepContext getStepContext();

    Item[] getItems();

    void setItems(final Item[] items);
}
