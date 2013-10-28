package io.machinecode.nock.spi.context;

import io.machinecode.nock.spi.RestartableJobExecution;
import io.machinecode.nock.spi.work.JobWork;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Context extends Serializable {

    JobWork getJob();

    RestartableJobExecution getJobExecution();

    long getJobInstanceId();

    long getJobExecutionId();

    long[] getStepExecutionIds();

    void setStepExecutionIds(final long[] stepExecutionIds);

    MutableJobContext getJobContext();

    void setJobContext(MutableJobContext jobContext);

    void setParentJobContext(MutableJobContext jobContext);

    MutableStepContext getStepContext();

    void setStepContext(final MutableStepContext stepContext);

    Throwable getThrowable();

    void setThrowable(Throwable thrown);
}
