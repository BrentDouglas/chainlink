package io.machinecode.chainlink.repository.infinispan;

import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;

import javax.batch.runtime.JobExecution;
import java.util.ArrayList;
import java.util.List;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class JobExecutionsForJobInstanceCallable extends BaseCallable<Long, ExtendedJobExecution, List<JobExecution>> {

    private final long jobInstanceId;

    public JobExecutionsForJobInstanceCallable(final long jobInstanceId) {
        this.jobInstanceId = jobInstanceId;
    }

    @Override
    public List<JobExecution> call() throws Exception {
        final List<JobExecution> ret = new ArrayList<JobExecution>();
        for (final ExtendedJobExecution jobExecution : cache.values()) {
            if (jobInstanceId == jobExecution.getJobInstanceId()) {
                ret.add(jobExecution);
            }
        }
        return ret;
    }
}
