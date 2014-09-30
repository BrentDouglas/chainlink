package io.machinecode.chainlink.repository.hazelcast;

import com.hazelcast.map.AbstractEntryProcessor;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;

import java.io.Serializable;
import java.util.Map;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class JobExecutionsForJobInstanceProcessor extends AbstractEntryProcessor<Long, ExtendedJobExecution> implements Serializable {

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
