package io.machinecode.chainlink.spi.repository;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.Metric;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface BaseExecution {

    BatchStatus getBatchStatus();

    Date getCreateTime();

    Date getStartTime();

    Date getUpdatedTime();

    Date getEndTime();

    String getExitStatus();

    Serializable getPersistentUserData();

    Metric[] getMetrics();

    Serializable getReaderCheckpoint();

    Serializable getWriterCheckpoint();
}
