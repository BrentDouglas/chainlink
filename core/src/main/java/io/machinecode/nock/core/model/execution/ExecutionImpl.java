package io.machinecode.nock.core.model.execution;

import io.machinecode.nock.core.work.ExecutionExecutable;
import io.machinecode.nock.core.work.Statuses;
import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.context.MutableJobContext;
import io.machinecode.nock.spi.context.ThreadId;
import io.machinecode.nock.spi.deferred.Deferred;
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
public abstract class ExecutionImpl implements io.machinecode.nock.spi.element.execution.Execution, ExecutionWork {

    private static final Logger log = Logger.getLogger(ExecutionImpl.class);

    protected final String id;

    public ExecutionImpl(final String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    public TransitionWork transition(final ExecutionContext context, final List<? extends TransitionWork> transitions,
                             final BatchStatus batchStatus, final String exitStatus) throws Exception {
        final String actualStatus = exitStatus == null ? batchStatus.name() : exitStatus;
        log().tracef(Messages.get("NOCK-009103.execution.transition.statuses"), context, id, exitStatus);
        for (final TransitionWork transition : transitions) {
            if (Statuses.matches(transition.getOn(), actualStatus)) {
                log().tracef(Messages.get("NOCK-009101.execution.transition.matched"), context, id, transition.element(), actualStatus, transition.getOn());
                if (transition.isTerminating()) {
                    final MutableJobContext jobContext = context.getJobContext();
                    jobContext.setBatchStatus(transition.getBatchStatus());
                    final String transitionExitStatus = transition.getExitStatus();
                    if (transitionExitStatus != null) {
                        jobContext.setExitStatus(transitionExitStatus);
                    }
                    context.setRestartElementId(transition.getRestartId());
                }
                return transition;
            } else {
                log().tracef(Messages.get("NOCK-009102.execution.transition.skipped"), context, id, transition.element(), actualStatus, transition.getOn());
            }
        }
        log().tracef(Messages.get("NOCK-009104.execution.no.transition.matched"), context, id, actualStatus);
        return null;
    }

    public Deferred<?> next(final Executor executor, final ThreadId threadId, final ExecutionContext context,
                            final Executable callback, final String next, final TransitionWork transition) throws Exception {
        final MutableJobContext jobContext = context.getJobContext();
        final BatchStatus batchStatus = jobContext.getBatchStatus();
        if (Statuses.isStopping(context) || Statuses.isFailed(batchStatus)) {
            return runCallback(executor, context, callback);
        }
        if (transition != null && transition.getNext() != null) {
            log().debugf(Messages.get("NOCK-009100.execution.transition"), context, id, transition.getNext());
            return _runNextExecution(executor, callback, context, threadId, transition.getNext());
        } else if (next != null) {
            log().debugf(Messages.get("NOCK-009100.execution.transition"), context, id, next);
            return _runNextExecution(executor, callback, context, threadId, next);
        } else {
            return runCallback(executor, context, callback);
        }
    }

    protected Deferred<?> runCallback(final Executor executor, final ExecutionContext context, final Executable callback) {
        return executor.callback(callback, context);
    }

    private Deferred<?> _runNextExecution(final Executor executor, final Executable callback, final ExecutionContext context,
                                          final ThreadId threadId, final String next) throws Exception {
        final ExecutionWork execution = context.getJob().getNextExecution(next);
        if (execution == null) {
            throw new IllegalStateException(Messages.format("NOCK-009000.execution.transition.invalid", context, id, next));
        }
        return executor.execute(new ExecutionExecutable(callback, execution, context, threadId));
    }

    protected Logger log() {
        return log;
    }
}
