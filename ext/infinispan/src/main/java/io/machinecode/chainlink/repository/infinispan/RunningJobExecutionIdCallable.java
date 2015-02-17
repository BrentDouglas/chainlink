package io.machinecode.chainlink.repository.infinispan;

import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;

import java.util.ArrayList;
import java.util.List;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class RunningJobExecutionIdCallable extends BaseCallable<Long, ExtendedJobExecution, List<Long>> {
    private static final long serialVersionUID = 1L;

    private final String jobName;

    public RunningJobExecutionIdCallable(final String jobName) {
        this.jobName = jobName;
    }

    @Override
    public List<Long> call() throws Exception {
        final List<Long> ids = new ArrayList<>();
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
