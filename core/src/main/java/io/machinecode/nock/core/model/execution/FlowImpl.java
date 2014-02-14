package io.machinecode.nock.core.model.execution;

import io.machinecode.nock.core.model.transition.TransitionImpl;
import io.machinecode.nock.core.work.ExecutionExecutable;
import io.machinecode.nock.core.work.Statuses;
import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.context.MutableJobContext;
import io.machinecode.nock.spi.context.ThreadId;
import io.machinecode.nock.spi.deferred.Deferred;
import io.machinecode.nock.spi.element.execution.Flow;
import io.machinecode.nock.spi.execution.Executable;
import io.machinecode.nock.spi.execution.Executor;
import io.machinecode.nock.spi.util.Messages;
import io.machinecode.nock.spi.work.ExecutionWork;
import io.machinecode.nock.spi.work.TransitionWork;
import org.jboss.logging.Logger;

import javax.batch.runtime.BatchStatus;
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
    public Deferred<?> before(final Executor executor, final ThreadId threadId, final Executable thisCallback,
                              final Executable parentCallback, final ExecutionContext context) throws Exception {
        log.debugf(Messages.get("NOCK-020000.flow.before"), context, id);
        final ExecutionWork execution = this.executions.get(0);
        return executor.execute(
                new ExecutionExecutable(thisCallback, execution, context)
        );
    }

    @Override
    public Deferred<?> after(final Executor executor, final ThreadId threadId, final Executable callback,
                             final ExecutionContext context, final ExecutionContext childContext) throws Exception {
        log.debugf(Messages.get("NOCK-020001.flow.after"), context, id);
        final MutableJobContext jobContext = context.getJobContext();
        final BatchStatus batchStatus = jobContext.getBatchStatus();
        if (Statuses.isStopping(batchStatus) || Statuses.isFailed(batchStatus)) {
            return runCallback(executor, context, callback);
        }
        final TransitionWork transition = this.transition(context, this.transitions, jobContext.getBatchStatus(), jobContext.getExitStatus());
        if (transition != null && transition.isTerminating()) {
            return runCallback(executor, context, callback);
        } else {
            return this.next(executor, threadId, context, callback, this.next, transition);
        }
    }

    @Override
    protected Logger log() {
        return log;
    }
}
