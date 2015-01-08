package io.machinecode.chainlink.core.element.execution;

import io.machinecode.chainlink.core.util.Statuses;
import io.machinecode.chainlink.core.work.ExecutionExecutable;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.context.MutableJobContext;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.work.ExecutionWork;
import io.machinecode.chainlink.spi.work.TransitionWork;
import org.jboss.logging.Logger;

import javax.batch.runtime.BatchStatus;
import java.io.Serializable;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public abstract class ExecutionImpl implements io.machinecode.chainlink.spi.element.execution.Execution, ExecutionWork, Serializable {
    private static final long serialVersionUID = 1L;

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
        log().tracef(Messages.get("CHAINLINK-009103.execution.transition.statuses"), context, id, exitStatus);
        for (final TransitionWork transition : transitions) {
            if (Statuses.matches(transition.getOn(), actualStatus)) {
                log().tracef(Messages.get("CHAINLINK-009101.execution.transition.matched"), context, id, transition.element(), actualStatus, transition.getOn());
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
                log().tracef(Messages.get("CHAINLINK-009102.execution.transition.skipped"), context, id, transition.element(), actualStatus, transition.getOn());
            }
        }
        log().tracef(Messages.get("CHAINLINK-009104.execution.no.transition.matched"), context, id, actualStatus);
        return null;
    }

    public Chain<?> next(final Configuration configuration, final WorkerId workerId, final ExecutionContext context,
                         final ExecutableId parentId, final ExecutionRepositoryId executionRepositoryId, final String next,
                         final TransitionWork transition) throws Exception {
        final MutableJobContext jobContext = context.getJobContext();
        final BatchStatus batchStatus = jobContext.getBatchStatus();
        if (Statuses.isStopping(context) || Statuses.isFailed(batchStatus)) {
            return configuration.getExecutor().callback(parentId, context);
        }
        if (transition != null && transition.getNext() != null) {
            log().debugf(Messages.get("CHAINLINK-009100.execution.transition"), context, id, transition.getNext());
            return _runNextExecution(configuration, parentId, context, workerId, executionRepositoryId, transition.getNext());
        } else if (next != null) {
            log().debugf(Messages.get("CHAINLINK-009100.execution.transition"), context, id, next);
            return _runNextExecution(configuration, parentId, context, workerId, executionRepositoryId, next);
        } else {
            return configuration.getExecutor().callback(parentId, context);
        }
    }

    private Chain<?> _runNextExecution(final Configuration configuration, final ExecutableId parentId, final ExecutionContext context,
                                       final WorkerId workerId, final ExecutionRepositoryId executionRepositoryId, final String next) throws Exception {
        final ExecutionWork execution = context.getJob().getNextExecution(next);
        if (execution == null) {
            throw new IllegalStateException(Messages.format("CHAINLINK-009000.execution.transition.invalid", context, id, next));
        }
        return configuration.getExecutor().execute(new ExecutionExecutable(
                parentId,
                execution,
                context,
                executionRepositoryId,
                workerId
        ));
    }

    protected Logger log() {
        return log;
    }
}
