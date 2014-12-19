package io.machinecode.chainlink.repository.hazelcast;

import com.hazelcast.map.AbstractEntryProcessor;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;

import java.util.Map;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class JobExecutionsForJobInstanceProcessor extends AbstractEntryProcessor<Long, ExtendedJobExecution> {
    private static final long serialVersionUID = 1L;

    final long jobInstanceId;

    public JobExecutionsForJobInstanceProcessor(final long jobInstanceId) {
        this.jobInstanceId = jobInstanceId;
    }

    @Override
    public Object process(final Map.Entry<Long, ExtendedJobExecution> entry) {
        final ExtendedJobExecution jobExecution = entry.getValue();
        if (jobInstanceId == jobExecution.getJobInstanceId()) {
            return jobExecution;
        }
        return null;
    }
}
