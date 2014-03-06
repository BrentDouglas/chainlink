package io.machinecode.chainlink.core.context;

import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.context.MutableJobContext;
import io.machinecode.chainlink.spi.context.MutableStepContext;
import io.machinecode.chainlink.spi.context.Item;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.work.JobWork;
import org.jboss.logging.Logger;

/**
 * Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ExecutionContextImpl implements ExecutionContext {

    private static final Logger log = Logger.getLogger(ExecutionContextImpl.class);

    private final JobWork job;
    private final ExtendedJobExecution jobExecution;
    private final ExtendedJobExecution restartJobExecution;
    private final long jobExecutionId;
    private final Long partitionExecutionId;
    private final MutableJobContext jobContext;
    private String restartElementId;
    private MutableStepContext stepContext;
    private Long stepExecutionId;
    private Item[] items;
    private Long lastStepExecutionId;
    private TLongSet priorStepExecutionIds = new TLongHashSet();

    private String logString;

    private ExecutionContextImpl(final JobWork job, final MutableJobContext jobContext, final ExtendedJobExecution jobExecution,
                                 final ExtendedJobExecution restartJobExecution, final Long partitionExecutionId, final Item[] items) {
        this.job = job;
        this.jobExecution = jobExecution;
        this.restartJobExecution = restartJobExecution;
        this.partitionExecutionId = partitionExecutionId;
        this.jobExecutionId = jobExecution.getExecutionId();
        this.jobContext = jobContext;
        this.items = items;
    }

    public ExecutionContextImpl(final JobWork job, final MutableJobContext jobContext, final MutableStepContext stepContext,
                                final ExtendedJobExecution jobExecution, final ExtendedJobExecution restartJobExecution,
                                final Long partitionExecutionId) {
        this(
                job,
                jobContext,
                jobExecution,
                restartJobExecution,
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
        return logString;
    }

    @Override
    public JobWork getJob() {
        return job;
    }

    @Override
    public ExtendedJobExecution getJobExecution() {
        return jobExecution;
    }

    @Override
    public ExtendedJobExecution getRestartJobExecution() {
        return restartJobExecution;
    }

    @Override
    public boolean isRestarting() {
        return restartJobExecution != null;
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
        return priorStepExecutionIds.toArray();
    }

    @Override
    public void addPriorStepExecutionId(final long priorStepExecutionId) {
        this.priorStepExecutionIds.add(priorStepExecutionId);
    }
}
