package io.machinecode.chainlink.spi.repository;

import javax.batch.runtime.JobExecution;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ExtendedJobExecution extends JobExecution {

    /**
     * @return The id of the step to restart on if this job was stopped.
     */
    String getRestartElementId();
}
