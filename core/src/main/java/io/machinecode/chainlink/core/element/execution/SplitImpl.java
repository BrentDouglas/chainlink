package io.machinecode.chainlink.core.element.execution;

import io.machinecode.chainlink.core.context.ExecutionContextImpl;
import io.machinecode.chainlink.core.context.JobContextImpl;
import io.machinecode.chainlink.core.work.ExecutionExecutable;
import io.machinecode.chainlink.core.util.Statuses;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.context.ThreadId;
import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.element.execution.Split;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.util.Messages;
import org.jboss.logging.Logger;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class SplitImpl extends ExecutionImpl implements Split {

    private static final Logger log = Logger.getLogger(SplitImpl.class);

    private final String next;
    private final List<FlowImpl> flows;

    public SplitImpl(final String id, final String next, final List<FlowImpl> flows) {
        super(id);
        this.next = next;
        this.flows = flows;
    }

    @Override
    public String getNext() {
        return this.next;
    }

    @Override
    public List<FlowImpl> getFlows() {
        return this.flows;
    }

    // Lifecycle

    private transient int _completed;

    @Override
    public Deferred<?> before(final Executor executor, final ThreadId threadId, final Executable thisCallback,
                              final Executable parentCallback, final ExecutionContext context) throws Exception {
        log.debugf(Messages.get("CHAINLINK-021000.split.before"), context, this.id);
        if (Statuses.isStopping(context) || Statuses.isComplete(context)) {
            return runCallback(executor, context, parentCallback);
        }
        final ExecutionExecutable[] flows = new ExecutionExecutable[this.flows.size()];
        for (int i = 0; i < flows.length; ++i) {
            final FlowImpl flow = this.flows.get(i);
            final ExecutionContext flowContext = new ExecutionContextImpl(
                    context.getJob(),
                    new JobContextImpl(context.getJobContext()),
                    null,
                    context.getJobExecutionId(),
                    context.getRestartJobExecutionId(),
                    context.getRestartElementId(),
                    null
            );
            flows[i] = new ExecutionExecutable(thisCallback, flow, flowContext);
        }
        this._completed = 0;
        return executor.execute(
                flows.length,
                flows
        );
    }

    @Override
    public Deferred<?> after(final Executor executor, final ThreadId threadId, final Executable callback,
                             final ExecutionContext context, final ExecutionContext childContext) throws Exception {
        log.debugf(Messages.get("CHAINLINK-021001.split.after"), context, this.id);
        final Long stepExecutionId = childContext.getLastStepExecutionId();
        if (stepExecutionId != null) {
            context.addPriorStepExecutionId(stepExecutionId);
        }
        if (++this._completed < this.flows.size()) {
            return null;
        }
        if (Statuses.isStopping(context) || Statuses.isComplete(context)) {
            return runCallback(executor, context, callback);
        }
        return this.next(executor, threadId, context, callback, this.next, null);
    }

    @Override
    protected Logger log() {
        return log;
    }
}
