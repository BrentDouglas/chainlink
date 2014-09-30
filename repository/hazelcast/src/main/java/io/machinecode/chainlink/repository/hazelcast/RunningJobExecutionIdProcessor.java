package io.machinecode.chainlink.repository.hazelcast;

import com.hazelcast.map.AbstractEntryProcessor;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;

import java.io.Serializable;
import java.util.Map;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class RunningJobExecutionIdProcessor extends AbstractEntryProcessor<Long, ExtendedJobExecution> implements Serializable {

    final String jobName;

    public RunningJobExecutionIdProcessor(final String jobName) {
        this.jobName = jobName;
    }

    @Override
    public Object process(final Map.Entry<Long, ExtendedJobExecution> entry) {
        final ExtendedJobExecution jobExecution = entry.getValue();
        if (jobName.equals(jobExecution.getJobName())) {
            switch (jobExecution.getBatchStatus()) {
                case STARTING:
                case STARTED:
                    return jobExecution.getExecutionId();
            }
        }
        return null;
    }
}
