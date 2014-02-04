package io.machinecode.nock.core.model.execution;

import io.machinecode.nock.core.impl.ExecutionContextImpl;
import io.machinecode.nock.core.work.RepositoryStatus;
import io.machinecode.nock.core.work.ExecutionExecutable;
import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.context.ThreadId;
import io.machinecode.nock.spi.deferred.Deferred;
import io.machinecode.nock.spi.element.execution.Split;
import io.machinecode.nock.spi.execution.CallbackExecutable;
import io.machinecode.nock.spi.execution.Executable;
import io.machinecode.nock.spi.execution.Executor;
import io.machinecode.nock.spi.util.Messages;
import io.machinecode.nock.spi.work.TransitionWork;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.Collections;
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

    private transient List<ExecutionContext> contexts;

    @Override
    public Deferred<?> before(final Executor executor, final ThreadId threadId, final CallbackExecutable thisExecutable,
                                 final CallbackExecutable parentExecutable, final ExecutionContext context,
                                 final ExecutionContext... previousContexts) throws Exception {
        log.debugf(Messages.get("split.run"), context.getJobExecutionId(), id);
        if (RepositoryStatus.isStopping(context) || RepositoryStatus.isComplete(context)) {
            return null; //TODO
        }
        final ExecutionExecutable[] flows = new ExecutionExecutable[this.flows.size()];
        for (int i = 0; i < flows.length; ++i) {
            final FlowImpl flow = this.flows.get(i);
            final ExecutionContext flowContext = new ExecutionContextImpl(
                    context,
                    flow.getId(),
                    null,
                    i
            );
            flows[i] = new ExecutionExecutable(flow, flowContext);
        }
        this.contexts = new ArrayList<ExecutionContext>(flows.length);
        return executor.execute(flows.length, flows);
    }

    @Override
    public Deferred<?> after(final Executor executor, final ThreadId threadId, final CallbackExecutable thisExecutable,
                                final CallbackExecutable parentExecutable, final ExecutionContext context,
                                final ExecutionContext childContext) throws Exception {
        log.debugf(Messages.get("split.after"), context.getJobExecutionId(), id);
        this.contexts.add(childContext);
        if (this.contexts.size() <= this.flows.size()) {
            return null; //TODO
        }
        return this.transition(executor, threadId, context, parentExecutable, Collections.<TransitionWork>emptyList(), this.next, null);
    }
}
