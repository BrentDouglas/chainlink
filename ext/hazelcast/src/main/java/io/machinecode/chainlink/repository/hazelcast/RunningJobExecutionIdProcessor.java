package io.machinecode.chainlink.repository.hazelcast;

import com.hazelcast.map.AbstractEntryProcessor;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;

import java.util.Map;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class RunningJobExecutionIdProcessor extends AbstractEntryProcessor<Long, ExtendedJobExecution> {
    private static final long serialVersionUID = 1L;

    final String jobName;

    public RunningJobExecutionIdProcessor(final String jobName) {
        this.jobName = jobName;
    }

    @Override
    public Object process(final Map.Entry<Long, ExtendedJobExecution> entry) {
        final ExtendedJobExecution jobExecution = entry.getValue();
        if (!jobName.equals(jobExecution.getJobName())) {
            return null;
        }
        switch (jobExecution.getBatchStatus()) {
            case STARTING:
            case STARTED:
            case STOPPING:
                return jobExecution.getExecutionId();
            default:
                return null;
        }
    }
}
