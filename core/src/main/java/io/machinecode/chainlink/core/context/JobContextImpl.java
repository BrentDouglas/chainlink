/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @author tags. All rights reserved.
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
package io.machinecode.chainlink.core.context;

import io.machinecode.chainlink.spi.Messages;
import org.jboss.logging.Logger;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;
import javax.batch.runtime.context.JobContext;
import java.io.Serializable;
import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JobContextImpl implements JobContext, Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(JobContextImpl.class);

    private final String jobName;
    private final long jobInstanceId;
    private final long jobExecutionId;
    private final Properties properties;
    private transient Object transientUserData;
    private BatchStatus batchStatus;
    private String exitStatus;

    public JobContextImpl(final JobContext context) {
        this.jobName = context.getJobName();
        this.jobInstanceId = context.getInstanceId();
        this.jobExecutionId = context.getExecutionId();
        this.properties = context.getProperties();
        this.batchStatus = BatchStatus.STARTED;
        this.exitStatus = null;
    }

    public JobContextImpl(final JobInstance instance, final JobExecution execution, final Properties properties) {
        this.jobName = instance.getJobName();
        this.jobInstanceId = instance.getInstanceId();
        this.jobExecutionId = execution.getExecutionId();
        this.properties = properties;
        this.batchStatus = BatchStatus.STARTING;
        this.exitStatus = null;
    }

    @Override
    public String getJobName() {
        return jobName;
    }

    @Override
    public Object getTransientUserData() {
        return transientUserData;
    }

    @Override
    public void setTransientUserData(final Object data) {
        this.transientUserData = data;
    }

    @Override
    public long getInstanceId() {
        return jobInstanceId;
    }

    @Override
    public long getExecutionId() {
        return jobExecutionId;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public BatchStatus getBatchStatus() {
        return batchStatus;
    }

    public void setBatchStatus(final BatchStatus batchStatus) {
        log.debugf(Messages.get("CHAINLINK-029000.job.context.batch.status"), jobExecutionId, jobName, batchStatus);
        this.batchStatus = batchStatus;
    }

    @Override
    public String getExitStatus() {
        return exitStatus;
    }

    @Override
    public void setExitStatus(final String exitStatus) {
        log.debugf(Messages.get("CHAINLINK-029001.job.context.exit.status"), jobExecutionId, jobName, exitStatus);
        this.exitStatus = exitStatus;
    }
}
