package io.machinecode.chainlink.spi.management;

import javax.batch.runtime.JobExecution;
import java.util.concurrent.Future;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface JobOperation extends Future<JobExecution> {

    long getJobExecutionId();
}
