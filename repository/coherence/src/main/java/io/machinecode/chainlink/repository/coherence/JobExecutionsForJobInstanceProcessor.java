package io.machinecode.chainlink.repository.coherence;

import com.tangosol.util.InvocableMap;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class JobExecutionsForJobInstanceProcessor implements InvocableMap.EntryProcessor, Serializable {

    final long jobInstanceId;

    public JobExecutionsForJobInstanceProcessor(final long jobInstanceId) {
        this.jobInstanceId = jobInstanceId;
    }

    @Override
    public Object process(final InvocableMap.Entry entry) {
        final ExtendedJobExecution jobExecution = (ExtendedJobExecution) entry.getValue();
        if (jobInstanceId == jobExecution.getJobInstanceId()) {
            return jobExecution;
        }
        return null;
    }

    @Override
    public Map processAll(final Set set) {
        return null;
    }
}
