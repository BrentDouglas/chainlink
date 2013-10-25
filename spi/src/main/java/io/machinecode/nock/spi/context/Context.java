package io.machinecode.nock.spi.context;

import io.machinecode.nock.spi.util.Pair;
import io.machinecode.nock.spi.work.JobWork;

import java.io.Serializable;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Context extends Serializable {

    JobWork getJob();

    long getJobInstanceId();

    long getJobExecutionId();

    long[] getStepExecutionIds();

    void setStepExecutionIds(final long[] stepExecutionIds);

    MutableJobContext getJobContext();

    void setJobContext(final MutableJobContext jobContext);

    MutableStepContext getStepContext();

    void setStepContext(final MutableStepContext stepContext);

    Throwable getThrowable();

    void setThrowable(Throwable thrown);

    List<? extends Pair<String, String>> getProperties();
}
