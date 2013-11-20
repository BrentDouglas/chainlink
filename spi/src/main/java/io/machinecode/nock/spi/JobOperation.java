package io.machinecode.nock.spi;

import javax.batch.runtime.JobExecution;
import java.util.concurrent.Future;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface JobOperation extends Future<JobExecution> {

    long getJobExecutionId();
}
