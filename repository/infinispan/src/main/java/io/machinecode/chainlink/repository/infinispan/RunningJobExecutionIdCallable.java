package io.machinecode.chainlink.repository.infinispan;

import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;

import javax.batch.runtime.JobExecution;
import java.util.ArrayList;
import java.util.List;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class RunningJobExecutionIdCallable extends BaseCallable<Long, ExtendedJobExecution, List<Long>> {

    private final String jobName;

    public RunningJobExecutionIdCallable(final String jobName) {
        this.jobName = jobName;
    }

    @Override
    public List<Long> call() throws Exception {
        final List<Long> ids = new ArrayList<Long>();
        for (final ExtendedJobExecution jobExecution : cache.values()) {
            if (jobName.equals(jobExecution.getJobName())) {
                switch (jobExecution.getBatchStatus()) {
                    case STARTING:
                    case STARTED:
                        ids.add(jobExecution.getExecutionId());
                }
            }
        }
        return ids;
    }
}
