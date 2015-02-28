package io.machinecode.chainlink.repository.coherence;

import com.tangosol.util.InvocableMap;

import javax.batch.runtime.JobInstance;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class JobInstanceProcessor extends BaseProcessor {
    private static final long serialVersionUID = 1L;

    final String jobName;

    public JobInstanceProcessor(final String jobName) {
        this.jobName = jobName;
    }

    @Override
    public Object process(final InvocableMap.Entry entry) {
        final JobInstance jobInstance = (JobInstance)entry.getValue();
        if (jobName.equals(jobInstance.getJobName())) {
            return jobInstance;
        }
        return null;
    }
}
