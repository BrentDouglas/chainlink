package io.machinecode.nock.core.impl;

import io.machinecode.nock.spi.RestartableJobExecution;
import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.context.MutableJobContext;
import io.machinecode.nock.spi.context.MutableStepContext;
import io.machinecode.nock.spi.context.ThreadId;
import io.machinecode.nock.spi.execution.Item;
import io.machinecode.nock.spi.work.JobWork;
import org.jboss.logging.Logger;

import java.util.Stack;

/**
 * Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ExecutionContextImpl implements ExecutionContext {

    private static final Logger log = Logger.getLogger(ExecutionContextImpl.class);

    private final JobWork job;
    private final RestartableJobExecution jobExecution;
    private final String id;
    private final int partition;
    private final long jobInstanceId;
    private final long jobExecutionId;
    private final Long stepExecutionId;
    private MutableJobContext jobContext;
    private MutableStepContext stepContext;
    private Item[] items;

    private ExecutionContextImpl(final JobWork job, final String id, final long jobInstanceId,
                                 final Long stepExecutionId, final int partition, final RestartableJobExecution jobExecution,
                                 final Item[] items) {
        this.job = job;
        this.id = id;
        this.jobInstanceId = jobInstanceId;
        this.jobExecution = jobExecution;
        this.jobExecutionId = jobExecution.getExecutionId();
        this.stepExecutionId = stepExecutionId;
        this.partition = partition;
        this.items = items;
    }

    public ExecutionContextImpl(final ExecutionContext context, final String id, final Long stepExecutionId, final int partition) {
        this(
                context.getJob(),
                id,
                context.getJobInstanceId(),
                stepExecutionId,
                partition,
                context.getJobExecution(),
                null
        );
        this.jobContext = context.getJobContext().copy();
        //this.stepContext = new StepContextImpl();
    }

    public ExecutionContextImpl(final ExecutionContext context, final String id, final Long stepExecutionId) {
        this(
                context.getJob(),
                id,
                context.getJobInstanceId(),
                stepExecutionId,
                -1,
                context.getJobExecution(),
                null
        );
        this.jobContext = context.getJobContext().copy();
        //this.stepContext = new StepContextImpl();
    }

    public ExecutionContextImpl(final JobWork job, final String id, final long jobInstanceId, final RestartableJobExecution jobExecution) {
        this(
                job,
                id,
                jobInstanceId,
                null,
                -1,
                jobExecution,
                null
        );
    }

    @Override
    public JobWork getJob() {
        return job;
    }

    @Override
    public RestartableJobExecution getJobExecution() {
        return jobExecution;
    }

    @Override
    public long getJobInstanceId() {
        return jobInstanceId;
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
    public String getId() {
        return id;
    }

    @Override
    public MutableJobContext getJobContext() {
        return jobContext;
    }

    @Override
    public void setJobContext(final MutableJobContext jobContext) {
        this.jobContext = jobContext;
    }

    @Override
    public MutableStepContext getStepContext() {
        return stepContext;
    }

    @Override
    public void setStepContext(final MutableStepContext stepContext) {
        this.stepContext = stepContext;
    }

    @Override
    public Item[] getItems() {
        return items;
    }

    @Override
    public void setItems(final Item[] items) {
        this.items = items;
    }
}
