/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @authors tag. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.machinecode.chainlink.core.repository;

import io.machinecode.chainlink.spi.marshalling.Marshalling;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;
import io.machinecode.chainlink.spi.repository.ExtendedStepExecution;
import io.machinecode.chainlink.spi.repository.PartitionExecution;

import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
class TestMapRepository extends BaseMapRepository {

    protected final Map<String, Long> ids;
    protected final Map<Long, ExtendedJobInstance> jobInstances;
    protected final Map<Long, ExtendedJobExecution> jobExecutions;
    protected final Map<Long, ExtendedStepExecution> stepExecutions;
    protected final Map<Long, PartitionExecution> partitionExecutions;
    protected final Map<Long, List<Long>> jobInstanceExecutions;
    protected final Map<Long, Long> jobExecutionInstances;
    protected final Map<Long, Set<Long>> jobExecutionStepExecutions;
    protected final Map<Long, Long> latestJobExecutionForInstance;
    protected final Map<Long, List<Long>> stepExecutionPartitionExecutions;
    protected final Map<Long, Set<Long>> jobExecutionHistory;

    public TestMapRepository(final Marshalling marshalling) {
        super(marshalling);

        this.ids = new HashMap<>();
        this.jobInstances = new HashMap<>();
        this.jobExecutions = new HashMap<>();
        this.stepExecutions = new HashMap<>();
        this.partitionExecutions = new HashMap<>();
        this.jobInstanceExecutions = new HashMap<>();
        this.jobExecutionInstances = new HashMap<>();
        this.jobExecutionStepExecutions = new HashMap<>();
        this.latestJobExecutionForInstance = new HashMap<>();
        this.stepExecutionPartitionExecutions = new HashMap<>();
        this.jobExecutionHistory = new HashMap<>();
    }

    @Override
    protected Map<String, Long> ids() {
        return this.ids;
    }

    @Override
    protected Map<Long, ExtendedJobInstance> jobInstances() {
        return this.jobInstances;
    }

    @Override
    protected Map<Long, ExtendedJobExecution> jobExecutions() {
        return this.jobExecutions;
    }

    @Override
    protected Map<Long, ExtendedStepExecution> stepExecutions() {
        return this.stepExecutions;
    }

    @Override
    protected Map<Long, PartitionExecution> partitionExecutions() {
        return this.partitionExecutions;
    }

    @Override
    protected Map<Long, List<Long>> jobInstanceExecutions() {
        return this.jobInstanceExecutions;
    }

    @Override
    protected Map<Long, Long> jobExecutionInstances() {
        return this.jobExecutionInstances;
    }

    @Override
    protected Map<Long, Set<Long>> jobExecutionStepExecutions() {
        return this.jobExecutionStepExecutions;
    }

    @Override
    protected Map<Long, Long> latestJobExecutionForInstance() {
        return this.latestJobExecutionForInstance;
    }

    @Override
    protected Map<Long, List<Long>> stepExecutionPartitionExecutions() {
        return this.stepExecutionPartitionExecutions;
    }

    @Override
    protected Map<Long, Set<Long>> jobExecutionHistory() {
        return this.jobExecutionHistory;
    }

    @Override
    protected long _id(final String key) throws Exception {
        final long id;
        synchronized (ids) {
            final Long that = ids.get(key);
            id = that == null ? 1 : that + 1;
            ids.put(key, id);
        }
        return id;
    }

    @Override
    protected Set<String> fetchJobNames() throws Exception {
        final Set<String> ret = new HashSet<>();
        for (final Map.Entry<Long, ExtendedJobInstance> entry : jobInstances.entrySet()) {
            ret.add(entry.getValue().getJobName());
        }
        return ret;
    }

    @Override
    protected int fetchJobInstanceCount(final String jobName) throws Exception {
        int count = 0;
        for (final Map.Entry<Long, ExtendedJobInstance> entry : jobInstances.entrySet()) {
            if (jobName.equals(entry.getValue().getJobName())) {
                ++count;
            }
        }
        return count;
    }

    @Override
    protected List<JobInstance> fetchJobInstances(final String jobName) throws Exception {
        final List<JobInstance> ret = new ArrayList<>();
        for (final Map.Entry<Long, ExtendedJobInstance> entry : jobInstances.entrySet()) {
            if (jobName.equals(entry.getValue().getJobName())) {
                ret.add(entry.getValue());
            }
        }
        return ret;
    }

    @Override
    protected List<Long> fetchRunningJobExecutionIds(final String jobName) throws Exception {
        final List<Long> ret = new ArrayList<>();
        for (final Map.Entry<Long, ExtendedJobExecution> entry : jobExecutions.entrySet()) {
            final ExtendedJobExecution value = entry.getValue();
            if (value.getJobName().equals(jobName)) {
                switch (value.getBatchStatus()) {
                    case STARTING:
                    case STARTED:
                    case STOPPING:
                        ret.add(value.getExecutionId());
                }
            }
        }
        return ret;
    }

    @Override
    protected List<JobExecution> fetchJobExecutionsForJobInstance(final long jobInstanceId) throws Exception {
        final List<JobExecution> ret = new ArrayList<>();
        for (final Map.Entry<Long, ExtendedJobExecution> entry : jobExecutions.entrySet()) {
            final ExtendedJobExecution value = entry.getValue();
            if (jobInstanceId == value.getJobInstanceId()) {
                ret.add(value);
            }
        }
        return ret;
    }

}
