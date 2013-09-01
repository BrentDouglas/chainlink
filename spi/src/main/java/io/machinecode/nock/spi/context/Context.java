package io.machinecode.nock.spi.context;

import io.machinecode.nock.spi.work.JobWork;

import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Context extends Serializable {

    JobWork getJob();

    long getJobInstanceId();

    long getJobExecutionId();

    long[] getStepExecutionIds();

    JobContext getJobContext();

    void setJobContext(final JobContext jobContext);

    StepContext getStepContext();

    void setStepContext(final StepContext stepContext);
}
