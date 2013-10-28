package io.machinecode.nock.spi;

import javax.batch.runtime.JobExecution;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface RestartableJobExecution extends JobExecution {

    /**
     * @return The id of the step to restart on if this job was stoped.
     */
    String getRestartId();
}
