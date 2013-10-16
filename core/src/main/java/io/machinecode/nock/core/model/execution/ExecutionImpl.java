package io.machinecode.nock.core.model.execution;

import io.machinecode.nock.core.work.DeferredImpl;
import io.machinecode.nock.core.work.PlanImpl;
import io.machinecode.nock.core.work.Status;
import io.machinecode.nock.core.work.execution.AfterExecution;
import io.machinecode.nock.core.work.execution.RunExecution;
import io.machinecode.nock.core.work.job.FailJob;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.element.execution.Execution;
import io.machinecode.nock.spi.transport.Plan;
import io.machinecode.nock.spi.transport.TargetThread;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.work.Deferred;
import io.machinecode.nock.spi.work.ExecutionWork;
import io.machinecode.nock.spi.work.TransitionWork;
import io.machinecode.nock.spi.work.TransitionWork.Result;

import javax.batch.runtime.BatchStatus;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class ExecutionImpl implements Execution, ExecutionWork {

    protected final String id;

    public ExecutionImpl(final String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Deferred before(final Transport transport, final Context context) throws Exception {
        return new DeferredImpl();
    }

    @Override
    public Deferred after(final Transport transport, final Context context) throws Exception {
        return new DeferredImpl();
    }

    @Override
    public Plan plan(final Transport transport, final Context context) {
        if (Status.isStopping(context) || Status.isComplete(context)) {
            return null; //TODO
        }
        final RunExecution run = new RunExecution(this, context);
        final AfterExecution after = new AfterExecution(this, context);
        final FailJob fail = new FailJob(context);

        after.register(transport.wrapSynchronization(run));

        final PlanImpl runPlan = new PlanImpl(run, TargetThread.ANY, element());
        final PlanImpl afterPlan = new PlanImpl(after, TargetThread.THIS, element());
        final PlanImpl failPlan = new PlanImpl(fail, TargetThread.THIS, element());

        runPlan.fail(failPlan)
                .always(afterPlan
                        .fail(failPlan));

        return runPlan;
    }


    public ExecutionWork transitionOrSetStatus(final Transport transport, final Context context, final List<? extends TransitionWork> transitions, final String next) throws Exception {
        if (next != null) {
            return context.getJob().next(next);
        }
        final BatchStatus status = transport.getRepository().getJobExecution(context.getJobExecutionId()).getBatchStatus();
        for (final TransitionWork transition : transitions) {
            if (Status.matches(transition.getOn(), status)) {
                final Result result = transition.runTransition();
                if (result.next != null) {
                    return context.getJob().next(result.next);
                } else {
                    Status.update(transport, context, result);
                    return null;
                }
            }
        }
        return null;
    }
}
