package io.machinecode.chainlink.core.element.execution;

import io.machinecode.chainlink.core.context.ExecutionContextImpl;
import io.machinecode.chainlink.core.context.JobContextImpl;
import io.machinecode.chainlink.core.work.ExecutionExecutable;
import io.machinecode.chainlink.core.util.Statuses;
import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.element.execution.Split;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.then.Chain;
import org.jboss.logging.Logger;

import java.util.List;

import static io.machinecode.chainlink.spi.registry.Registry.Accumulator;
import static io.machinecode.chainlink.spi.registry.Registry.SplitAccumulator;

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

    @Override
    public Chain<?> before(final RuntimeConfiguration configuration, final ExecutionRepositoryId executionRepositoryId,
                              final WorkerId workerId, final ExecutableId callbackId, final ExecutableId parentId,
                              final ExecutionContext context) throws Exception {
        log.debugf(Messages.get("CHAINLINK-021000.split.before"), context, this.id);
        if (Statuses.isStopping(context) || Statuses.isComplete(context)) {
            return runCallback(configuration, context, parentId);
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
            flows[i] = new ExecutionExecutable(
                    callbackId,
                    flow,
                    flowContext,
                    executionRepositoryId,
                    null
            );
        }
        return configuration.getExecutor().distribute(
                flows.length,
                flows
        );
    }

    @Override
    public Chain<?> after(final RuntimeConfiguration configuration, final ExecutionRepositoryId executionRepositoryId,
                             final WorkerId workerId, final ExecutableId parentId, final ExecutionContext context,
                             final ExecutionContext childContext) throws Exception {
        log.debugf(Messages.get("CHAINLINK-021001.split.after"), context, this.id);
        final long jobExecutionId = context.getJobExecutionId();
        final Long stepExecutionId = childContext.getLastStepExecutionId();
        final SplitAccumulator accumulator = configuration.getRegistry().getSplitAccumulator(jobExecutionId, this.id);
        if (stepExecutionId != null) {
            accumulator.addPriorStepExecutionId(stepExecutionId);
        }
        if (accumulator.incrementAndGetCallbackCount() < this.flows.size()) {
            return null;
        }
        context.setPriorStepExecutionId(accumulator.getPriorStepExecutionIds());
        if (Statuses.isStopping(context) || Statuses.isComplete(context)) {
            return runCallback(configuration, context, parentId);
        }
        return this.next(configuration, workerId, context, parentId, executionRepositoryId, this.next, null);
    }

    @Override
    protected Logger log() {
        return log;
    }
}
