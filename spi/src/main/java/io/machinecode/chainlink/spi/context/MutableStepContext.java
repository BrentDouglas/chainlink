package io.machinecode.chainlink.spi.context;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.Metric.MetricType;
import javax.batch.runtime.context.StepContext;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface MutableStepContext extends StepContext {

    void setException(Exception exception);

    void setBatchStatus(BatchStatus batchStatus);

    MutableMetric getMetric(MetricType type);
}
