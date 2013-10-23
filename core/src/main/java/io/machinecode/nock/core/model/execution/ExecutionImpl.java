package io.machinecode.nock.core.model.execution;

import io.machinecode.nock.core.work.PlanImpl;
import io.machinecode.nock.core.work.Status;
import io.machinecode.nock.core.work.execution.AfterExecution;
import io.machinecode.nock.core.work.execution.RunExecution;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.element.execution.Execution;
import io.machinecode.nock.spi.transport.Plan;
import io.machinecode.nock.spi.transport.TargetThread;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.work.ExecutionWork;
import io.machinecode.nock.spi.work.TransitionWork;
import io.machinecode.nock.spi.work.TransitionWork.Result;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.context.StepContext;
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
        //final FailJob fail = new FailJob(this, context); //TODO

        final PlanImpl runPlan = new PlanImpl(run, TargetThread.ANY, element());
        final PlanImpl afterPlan = new PlanImpl(after, TargetThread.THIS, element());
        //final PlanImpl failPlan = new PlanImpl(fail, TargetThread.THIS, element());

        runPlan//.fail(failPlan)
                .always(afterPlan
                        //.fail(failPlan)
                );

        return runPlan;
    }


    public ExecutionWork transitionOrSetStatus(final Transport transport, final Context context, final List<? extends TransitionWork> transitions, final String next) throws Exception {
        if (next != null) {
            return context.getJob().next(next);
        }
        final StepContext stepContext = context.getStepContext();
        final String exitStatus = stepContext.getExitStatus();
        final BatchStatus batchStatus = stepContext.getBatchStatus();
        final String status = exitStatus == null
                ? batchStatus.name()
                : exitStatus;
        for (final TransitionWork transition : transitions) {
            if (Status.matches(transition.getOn(), status)) {
                final Result result = transition.runTransition();
                if (result.next != null) {
                    return context.getJob().next(result.next);
                } else {
                    Status.finishStep(transport.getRepository(), stepContext.getStepExecutionId(), result.batchStatus, result.exitStatus);
                    return null;
                }
            }
        }
        Status.finishStep(transport.getRepository(), stepContext.getStepExecutionId(), BatchStatus.COMPLETED, exitStatus);
        return null;
    }
}
