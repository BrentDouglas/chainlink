package io.machinecode.chainlink.repository.hazelcast;

import com.hazelcast.map.AbstractEntryProcessor;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;

import java.io.Serializable;
import java.util.Map;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class JobInstanceCountProcessor extends AbstractEntryProcessor<Long, ExtendedJobInstance> implements Serializable {

    final String jobName;

    public JobInstanceCountProcessor(final String jobName) {
        this.jobName = jobName;
    }

    @Override
    public Object process(final Map.Entry<Long, ExtendedJobInstance> entry) {
        return jobName.equals(entry.getValue().getJobName());
    }
}
