package io.machinecode.chainlink.spi.repository;

import javax.batch.runtime.JobExecution;
import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface ExtendedJobExecution extends JobExecution, Serializable {

    /**
     * @return The id of the step to restart on if this job was stopped.
     */
    String getRestartElementId();

    long getJobInstanceId();
}
