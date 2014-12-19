package io.machinecode.chainlink.core.context;

import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.context.Item;
import io.machinecode.chainlink.spi.context.MutableJobContext;
import io.machinecode.chainlink.spi.context.MutableStepContext;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.work.JobWork;
import org.jboss.logging.Logger;

import java.io.Serializable;

/**
 * Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ExecutionContextImpl implements ExecutionContext, Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(ExecutionContextImpl.class);

    private final JobWork job;
    private final long jobExecutionId;
    private final Long restartJobExecutionId;
    private final Long partitionExecutionId;
    private final MutableJobContext jobContext;
    private String restartElementId;
    private MutableStepContext stepContext;
    private Long stepExecutionId;
    private Item[] items;
    private Long lastStepExecutionId;
    private long[] priorStepExecutionIds;

    private transient String logString;

    private ExecutionContextImpl(final JobWork job, final MutableJobContext jobContext, final long jobExecutionId,
                                 final Long restartJobExecutionId, final String restartElementId, final Long partitionExecutionId,
                                 final Item[] items) {
        this.job = job;
        this.jobExecutionId = jobExecutionId;
        this.restartJobExecutionId = restartJobExecutionId;
        this.restartElementId = restartElementId;
        this.partitionExecutionId = partitionExecutionId;
        this.jobContext = jobContext;
        this.items = items;
    }

    public ExecutionContextImpl(final JobWork job, final MutableJobContext jobContext, final MutableStepContext stepContext,
                                final long jobExecutionId, final Long restartJobExecutionId, final String restartElementId,
                                final Long partitionExecutionId) {
        this(
                job,
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
    public JobWork getJob() {
        return job;
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
    public MutableJobContext getJobContext() {
        return jobContext;
    }

    @Override
    public MutableStepContext getStepContext() {
        return stepContext;
    }

    @Override
    public void setStepContext(final MutableStepContext stepContext) {
        this.stepContext = stepContext;
        this.stepExecutionId = stepContext == null ? null : stepContext.getStepExecutionId();
        _buildLogString();
    }

    @Override
    public Item[] getItems() {
        return items;
    }

    @Override
    public void setItems(final Item[] items) {
        this.items = items;
    }

    @Override
    public String getRestartElementId() {
        return restartElementId;
    }

    @Override
    public void setRestartElementId(final String restartElementId) {
        this.restartElementId = restartElementId;
    }

    @Override
    public Long getLastStepExecutionId() {
        return this.lastStepExecutionId;
    }

    @Override
    public void setLastStepExecutionId(final long lastStepExecutionId) {
        this.lastStepExecutionId = lastStepExecutionId;
    }

    @Override
    public long[] getPriorStepExecutionIds() {
        return priorStepExecutionIds;
    }

    @Override
    public void setPriorStepExecutionId(final long[] priorStepExecutionId) {
        this.priorStepExecutionIds = priorStepExecutionId;
    }
}
