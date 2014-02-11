package io.machinecode.nock.core.model.execution;

import io.machinecode.nock.core.model.transition.TransitionImpl;
import io.machinecode.nock.core.work.ExecutionExecutable;
import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.context.ThreadId;
import io.machinecode.nock.spi.deferred.Deferred;
import io.machinecode.nock.spi.element.execution.Flow;
import io.machinecode.nock.spi.execution.CallbackExecutable;
import io.machinecode.nock.spi.execution.Executable;
import io.machinecode.nock.spi.execution.Executor;
import io.machinecode.nock.spi.util.Messages;
import io.machinecode.nock.spi.work.TransitionWork;
import org.jboss.logging.Logger;

import java.util.Collections;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FlowImpl extends ExecutionImpl implements Flow {

    private static final Logger log = Logger.getLogger(FlowImpl.class);

    private final String next;
    private final List<ExecutionImpl> executions;
    private final List<TransitionImpl> transitions;

    public FlowImpl(final String id, final String next, final List<ExecutionImpl> executions,
                    final List<TransitionImpl> transitions) {
        super(id);
        this.next = next;
        this.executions = executions;
        this.transitions = transitions;
    }

    @Override
    public String getNext() {
        return this.next;
    }

    @Override
    public List<ExecutionImpl> getExecutions() {
        return this.executions;
    }

    @Override
    public List<TransitionImpl> getTransitions() {
        return this.transitions;
    }

    // Lifecycle

    @Override
    public Deferred<?> before(final Executor executor, final ThreadId threadId, final CallbackExecutable thisExecutable,
                                 final CallbackExecutable parentExecutable, final ExecutionContext context,
                                 final ExecutionContext... contexts) throws Exception {
        log.debugf(Messages.get("flow.run"), context.getJobExecutionId(), id);
        return executor.execute(
                new ExecutionExecutable(thisExecutable, this.executions.get(0), context)
        );
    }

    @Override
    public Deferred<?> after(final Executor executor, final ThreadId threadId, final CallbackExecutable thisExecutable,
                                final CallbackExecutable parentExecutable, final ExecutionContext context,
                                final ExecutionContext childContext) throws Exception {
        log.debugf(Messages.get("flow.after"), context.getJobExecutionId(), id);
        return this.transition(executor, threadId, context, thisExecutable, parentExecutable, Collections.<TransitionWork>emptyList(), this.next, null);
    }
}
