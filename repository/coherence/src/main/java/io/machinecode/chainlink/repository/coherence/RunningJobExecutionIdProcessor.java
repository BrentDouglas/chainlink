package io.machinecode.chainlink.repository.coherence;

import com.tangosol.util.InvocableMap;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class RunningJobExecutionIdProcessor implements InvocableMap.EntryProcessor, Serializable {

    final String jobName;

    public RunningJobExecutionIdProcessor(final String jobName) {
        this.jobName = jobName;
    }
    @Override
    public Object process(final InvocableMap.Entry entry) {
        final ExtendedJobExecution jobExecution = (ExtendedJobExecution) entry.getValue();
        if (jobName.equals(jobExecution.getJobName())) {
            switch (jobExecution.getBatchStatus()) {
                case STARTING:
                case STARTED:
                    return jobExecution.getExecutionId();
            }
        }
        return null;
    }

    @Override
    public Map processAll(final Set set) {
        return null;
    }
}
