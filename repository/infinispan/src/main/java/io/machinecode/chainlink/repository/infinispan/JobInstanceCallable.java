package io.machinecode.chainlink.repository.infinispan;

import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;

import javax.batch.runtime.JobInstance;
import java.util.ArrayList;
import java.util.List;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class JobInstanceCallable extends BaseCallable<Long, ExtendedJobInstance, List<JobInstance>> {

    private final String jobName;

    public JobInstanceCallable(final String jobName) {
        this.jobName = jobName;
    }

    @Override
    public List<JobInstance> call() throws Exception {
        final List<JobInstance> jobInstances = new ArrayList<JobInstance>();
        for (final JobInstance jobInstance : cache.values()) {
            if (jobName.equals(jobInstance.getJobName())) {
                jobInstances.add(jobInstance);
            }
        }
        return jobInstances;
    }
}
