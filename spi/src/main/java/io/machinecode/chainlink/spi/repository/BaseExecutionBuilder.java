package io.machinecode.chainlink.spi.repository;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.Metric;
import java.io.Serializable;
import java.util.Date;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface BaseExecutionBuilder<T extends BaseExecutionBuilder<T>> {

    T setBatchStatus(final BatchStatus batchStatus);

    T setCreateTime(final Date createTime);

    T setStartTime(final Date startTime);

    T setUpdatedTime(final Date updatedTime);

    T setEndTime(final Date endTime);

    T setExitStatus(final String exitStatus);

    T setPersistentUserData(final Serializable persistentUserData);

    T setMetrics(final Metric[] metrics);

    T setReaderCheckpoint(final Serializable readerCheckpoint);

    T setWriterCheckpoint(final Serializable writerCheckpoint);
}
