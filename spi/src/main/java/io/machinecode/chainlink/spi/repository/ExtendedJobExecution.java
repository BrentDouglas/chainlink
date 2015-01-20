package io.machinecode.chainlink.spi.repository;

import javax.batch.runtime.JobExecution;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface ExtendedJobExecution extends JobExecution {

    /**
     * @return The id of the step to restart on if this job was stopped.
     */
    String getRestartElementId();

    long getJobInstanceId();
}
