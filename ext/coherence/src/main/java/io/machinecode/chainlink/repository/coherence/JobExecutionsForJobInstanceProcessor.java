package io.machinecode.chainlink.repository.coherence;

import com.tangosol.util.InvocableMap;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class JobExecutionsForJobInstanceProcessor extends BaseProcessor {
    private static final long serialVersionUID = 1L;

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
}
