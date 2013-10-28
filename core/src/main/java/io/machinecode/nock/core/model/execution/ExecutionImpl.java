package io.machinecode.nock.core.model.execution;

import io.machinecode.nock.core.work.PlanImpl;
import io.machinecode.nock.core.work.Status;
import io.machinecode.nock.core.work.execution.AfterExecution;
import io.machinecode.nock.core.work.execution.FailExecution;
import io.machinecode.nock.core.work.execution.RunExecution;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.context.MutableJobContext;
import io.machinecode.nock.spi.context.MutableStepContext;
import io.machinecode.nock.spi.element.execution.Execution;
import io.machinecode.nock.spi.transport.Plan;
import io.machinecode.nock.spi.transport.TargetThread;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.util.Message;
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
    public Plan before(final Transport transport, final Context context) throws Exception {
        return null;//new DeferredImpl<Void>();
    }

    @Override
    public Plan after(final Transport transport, final Context context) throws Exception {
        return null;//new DeferredImpl<Void>();
    }

    @Override
    public Plan plan(final Transport transport, final Context context) {
        if (Status.isStopping(context) || Status.isComplete(context)) {
            return null; //TODO
        }
        final RunExecution run = new RunExecution(this, context);
        final AfterExecution after = new AfterExecution(this, context);
        final FailExecution fail = new FailExecution(this, context); //TODO

        final PlanImpl runPlan = new PlanImpl(run, TargetThread.ANY, element());
        final PlanImpl afterPlan = new PlanImpl(after, TargetThread.THIS, element());
        final PlanImpl failPlan = new PlanImpl(fail, TargetThread.THIS, element());
        final PlanImpl afterFailPlan = new PlanImpl(fail, TargetThread.THIS, element());

        runPlan.fail(failPlan)
                .always(afterPlan
                        .fail(afterFailPlan)
                );

        return runPlan;
    }

    public String getExitStatus(final Context context) {
        final MutableStepContext stepContext = context.getStepContext();
        final MutableJobContext jobContext = context.getJobContext();
        final String exitStatus;
        final String batchletStatus;
        final BatchStatus batchStatus;
        if (stepContext != null) {
            batchStatus = stepContext.getBatchStatus();
            exitStatus = stepContext.getExitStatus();
            batchletStatus = stepContext.getBatchletStatus();
        } else if (jobContext != null) {
            batchStatus = jobContext.getBatchStatus();
            exitStatus = jobContext.getExitStatus();
            batchletStatus = null;
        } else {
            throw new IllegalStateException(); //TODO Message
        }
        return exitStatus != null
                ? exitStatus
                : batchletStatus != null
                    ? batchletStatus
                    : batchStatus.name();
    }

    public ExecutionWork transition(final Transport transport, final Context context, final List<? extends TransitionWork> transitions, final String next) throws Exception {
        final String status = getExitStatus(context);
        log.tracef(Message.get("execution.transition.statuses"), context.getJobExecutionId(), id, status);
        for (final TransitionWork transition : transitions) {
            if (Status.matches(transition.getOn(), status)) {
                log.tracef(Message.get("execution.transition.matched"), context.getJobExecutionId(), id, transition.element(), status, transition.getOn());
                final Result result = transition.runTransition();
                if (result.next != null) {
                    log.debugf(Message.get("execution.transition"), context.getJobExecutionId(), id, result.next);
                    Status.finishStep(transport.getRepository(), context.getJobExecutionId(), BatchStatus.COMPLETED, status);
                    return context.getJob().next(result.next);
                } else {
                    final String finalStatus = result.exitStatus == null ? status : result.exitStatus;
                    Status.finishStep(transport.getRepository(), context.getJobExecutionId(), result.batchStatus, finalStatus);
                    Status.finishJob(transport.getRepository(), context.getJobExecutionId(), result.batchStatus, finalStatus, result.restartId);
                    return null;
                }
            } else {
                log.tracef(Message.get("execution.transition.skipped"), context.getJobExecutionId(), id, transition.element(), status, transition.getOn());
            }
        }
        if (next != null) {
            return context.getJob().next(next);
        }
        return null;
    }
}
