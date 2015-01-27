package io.machinecode.chainlink.core.repository;

import io.machinecode.chainlink.spi.repository.ExtendedStepExecution;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.Metric;
import java.io.Serializable;
import java.util.Date;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface ExtendedStepExecutionBuilder<T extends ExtendedStepExecutionBuilder<T>> extends BaseExecutionBuilder<T> {

    T setStepExecutionId(final long stepExecutionId);

    T setStepName(final String stepName);

    T setBatchStatus(final BatchStatus batchStatus);

    T setStartTime(final Date startTime);

    T setEndTime(final Date endTime);

    T setExitStatus(final String exitStatus);

    T setPersistentUserData(final Serializable persistentUserData);

    T setMetrics(final Metric[] metrics);

    T setJobExecutionId(final long jobExecutionId);

    ExtendedStepExecution build();
}
