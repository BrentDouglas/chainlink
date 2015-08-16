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
package io.machinecode.chainlink.core.context;

import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.context.Item;
import org.jboss.logging.Logger;

import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class ExecutionContextImpl implements ExecutionContext, Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(ExecutionContextImpl.class);

    private final long jobExecutionId;
    private final Long restartJobExecutionId;
    private final Long partitionExecutionId;
    private final JobContextImpl jobContext;
    private String restartElementId;
    private StepContextImpl stepContext;
    private Long stepExecutionId;
    private Item[] items;
    private Long lastStepExecutionId;
    private long[] priorStepExecutionIds;

    private transient String logString;

    private ExecutionContextImpl(final JobContextImpl jobContext, final long jobExecutionId,
                                 final Long restartJobExecutionId, final String restartElementId, final Long partitionExecutionId,
                                 final Item[] items) {
        this.jobExecutionId = jobExecutionId;
        this.restartJobExecutionId = restartJobExecutionId;
        this.restartElementId = restartElementId;
        this.partitionExecutionId = partitionExecutionId;
        this.jobContext = jobContext;
        this.items = items;
    }

    public ExecutionContextImpl(final JobContextImpl jobContext, final StepContextImpl stepContext,
                                final long jobExecutionId, final Long restartJobExecutionId, final String restartElementId,
                                final Long partitionExecutionId) {
        this(
                jobContext,
                jobExecutionId,
                restartJobExecutionId,
                restartElementId,
                partitionExecutionId,
                null
        );
        this.stepContext = stepContext;
        this.stepExecutionId = stepContext == null ? null : stepContext.getStepExecutionId();
        _buildLogString();
    }

    private void _buildLogString() {
        final StringBuilder builder = new StringBuilder()
                .append(Messages.raw("prefix.job.execution")).append('=').append(this.jobExecutionId);
        if (this.stepExecutionId != null) {
            builder.append(',').append(Messages.raw("prefix.step.execution")).append('=').append(this.stepExecutionId);
        }
        if (this.partitionExecutionId != null) {
            builder.append(',').append(Messages.raw("prefix.partition")).append('=').append(this.partitionExecutionId);
        }
        this.logString = builder.toString();
    }

    @Override
    public String toString() {
        if (logString == null) {
            _buildLogString();
        }
        return logString;
    }

    @Override
    public Long getRestartJobExecutionId() {
        return restartJobExecutionId;
    }

    @Override
    public boolean isRestarting() {
        return restartJobExecutionId != null;
    }

    @Override
    public long getJobExecutionId() {
        return jobExecutionId;
    }

    @Override
    public Long getStepExecutionId() {
        return stepExecutionId;
    }

    @Override
    public Long getPartitionExecutionId() {
        return partitionExecutionId;
    }

    @Override
    public JobContextImpl getJobContext() {
        return jobContext;
    }

    @Override
    public StepContextImpl getStepContext() {
        return stepContext;
    }

    public void setStepContext(final StepContextImpl stepContext) {
        this.stepContext = stepContext;
        this.stepExecutionId = stepContext == null ? null : stepContext.getStepExecutionId();
        _buildLogString();
    }

    @Override
    public Item[] getItems() {
        return items;
    }

    public void setItems(final Item... items) {
        this.items = items;
    }

    @Override
    public String getRestartElementId() {
        return restartElementId;
    }

    public void setRestartElementId(final String restartElementId) {
        this.restartElementId = restartElementId;
    }

    @Override
    public Long getLastStepExecutionId() {
        return this.lastStepExecutionId;
    }

    public void setLastStepExecutionId(final long lastStepExecutionId) {
        this.lastStepExecutionId = lastStepExecutionId;
    }

    @Override
    public long[] getPriorStepExecutionIds() {
        return priorStepExecutionIds;
    }

    public void setPriorStepExecutionId(final long[] priorStepExecutionId) {
        this.priorStepExecutionIds = priorStepExecutionId;
    }
}
