package io.machinecode.chainlink.spi.repository;

import javax.batch.runtime.BatchStatus;
import java.util.Date;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ExtendedJobExecutionBuilder<T extends ExtendedJobExecutionBuilder<T>> {

    T setJobExecutionId(final long jobExecutionId);

    T setJobName(final String jobName);

    T setBatchStatus(final BatchStatus batchStatus);

    T setStartTime(final Date startTime);

    T setEndTime(final Date endTime);

    T setExitStatus(final String exitStatus);

    T setCreateTime(final Date createTime);

    T setLastUpdatedTime(final Date lastUpdatedTime);

    T setJobParameters(final Properties jobParameters);

    T setRestartElementId(final String restartElementId);

    T setJobInstanceId(final long jobInstanceId);

    ExtendedJobExecution build();
}