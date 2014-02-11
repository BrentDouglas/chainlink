package io.machinecode.nock.core.model.execution;

import io.machinecode.nock.core.work.ExecutionExecutable;
import io.machinecode.nock.core.work.RepositoryStatus;
import io.machinecode.nock.spi.ExecutionRepository;
import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.context.ThreadId;
import io.machinecode.nock.spi.deferred.Deferred;
import io.machinecode.nock.spi.element.execution.Execution;
import io.machinecode.nock.spi.execution.CallbackExecutable;
import io.machinecode.nock.spi.execution.Executable;
import io.machinecode.nock.spi.execution.Executor;
import io.machinecode.nock.spi.util.Messages;
import io.machinecode.nock.spi.work.ExecutionWork;
import io.machinecode.nock.spi.work.TransitionWork;
import io.machinecode.nock.spi.work.TransitionWork.Result;
import org.jboss.logging.Logger;

import javax.batch.runtime.BatchStatus;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class ExecutionImpl implements Execution, ExecutionWork {

    private static final Logger log = Logger.getLogger(ExecutionImpl.class);

    protected final String id;

    public ExecutionImpl(final String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public ExecutionContext createExecutionContext(final ExecutionRepository repository, final ExecutionContext parentContext) throws Exception {
        return parentContext;
    }

    public String getExitStatus(final String jobStatus, final String executionStatus) {
        return executionStatus != null
                ? executionStatus
                : jobStatus;
    }

    public Deferred<?> transition(final Executor executor, final ThreadId threadId, final ExecutionContext context,
                                  final CallbackExecutable thisExecutable, final CallbackExecutable parentExecutable, final List<? extends TransitionWork> transitions,
                                    final String next, final String executionStatus) throws Exception {
        //if (Status.isStopping(context)) {
        //    return null; //TODO log
        //}
        final long jobExecutionId = context.getJobExecutionId();
        final BatchStatus batchStatus = context.getStepContext().getBatchStatus();
        final String exitStatus = getExitStatus(context.getJobContext().getExitStatus(), executionStatus);
        log.tracef(Messages.get("execution.transition.statuses"), jobExecutionId, id, exitStatus);
        for (final TransitionWork transition : transitions) {
            if (RepositoryStatus.matches(transition.getOn(), exitStatus)) { //TODO No idea why see testTransitionElementOnAttrValuesWithRestartJobParamOverrides
                log.tracef(Messages.get("execution.transition.matched"), jobExecutionId, id, transition.element(), exitStatus, transition.getOn());
                final Result result = transition.runTransition(id);
                if (result.next != null) {
                    if (RepositoryStatus.isFailed(batchStatus)) {
                        RepositoryStatus.finishStep(executor.getRepository(), jobExecutionId, batchStatus, exitStatus);
                        return _runCallback(executor, context, parentExecutable);
                    }
                    log.debugf(Messages.get("execution.transition"), jobExecutionId, id, result.next);
                    RepositoryStatus.finishStep(executor.getRepository(), jobExecutionId, BatchStatus.COMPLETED, exitStatus);
                    return _runNextExecution(executor, thisExecutable, context, threadId, result.next);
                } else {
                    final String finalStatus = result.exitStatus == null ? exitStatus : result.exitStatus;
                    RepositoryStatus.finishStep(executor.getRepository(), jobExecutionId, result.batchStatus, finalStatus);
                    RepositoryStatus.finishJob(executor.getRepository(), jobExecutionId, result.batchStatus, finalStatus, result.restartId);
                    return _runCallback(executor, context, parentExecutable);
                }
            } else {
                log.tracef(Messages.get("execution.transition.skipped"), jobExecutionId, id, transition.element(), exitStatus, transition.getOn());
            }
        }
        if (RepositoryStatus.isFailed(batchStatus)) {
            RepositoryStatus.finishStep(executor.getRepository(), jobExecutionId, batchStatus, exitStatus);
            return _runCallback(executor, context, parentExecutable);
        }
        if (next != null) {
            return _runNextExecution(executor, thisExecutable, context, threadId, next);
        }
        return _runCallback(executor, context, parentExecutable);
    }

    private static Deferred<?> _runCallback(final Executor executor, final ExecutionContext context,
                                            final CallbackExecutable parentExecutable) {
        return executor.callback(parentExecutable, context);
    }

    private static Deferred<?> _runNextExecution(final Executor executor, final CallbackExecutable thisExecutable,
                                                 final ExecutionContext context, final ThreadId threadId, final String next) throws Exception {
        final ExecutionWork execution = context.getJob().getNextExecution(next);
        return executor.execute(
                threadId,
                new ExecutionExecutable(thisExecutable, execution, execution.createExecutionContext(executor.getRepository(), context))
        );
    }
}
