package io.machinecode.chainlink.repository.coherence;

import com.tangosol.util.InvocableMap;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class JobInstanceCountProcessor implements InvocableMap.EntryProcessor, Serializable {
    private static final long serialVersionUID = 1L;

    final String jobName;

    public JobInstanceCountProcessor(final String jobName) {
        this.jobName = jobName;
    }

    @Override
    public Object process(final InvocableMap.Entry entry) {
        return jobName.equals(((ExtendedJobInstance) entry.getValue()).getJobName());
    }

    @Override
    public Map processAll(final Set set) {
        return null;
    }
}
