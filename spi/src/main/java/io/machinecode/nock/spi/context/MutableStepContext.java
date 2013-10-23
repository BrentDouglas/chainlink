package io.machinecode.nock.spi.context;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.Metric;
import javax.batch.runtime.context.StepContext;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface MutableStepContext extends StepContext {

    void setException(Exception exception);

    void setBatchStatus(BatchStatus batchStatus);

    void setMetrics(Metric[] metrics);
}
