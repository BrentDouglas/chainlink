package io.machinecode.nock.core.work;

import io.machinecode.nock.core.work.execution.AfterExecution;
import io.machinecode.nock.core.work.execution.RunExecution;
import io.machinecode.nock.core.work.job.AfterJob;
import io.machinecode.nock.core.work.job.FailJob;
import io.machinecode.nock.core.work.job.RunJob;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.work.ExecutionWork;
import io.machinecode.nock.spi.work.JobWork;
import io.machinecode.nock.spi.work.PartitionWork;
import io.machinecode.nock.spi.work.TransitionWork;
import io.machinecode.nock.spi.work.TransitionWork.Result;
import io.machinecode.nock.spi.work.Worker;

import javax.batch.runtime.BatchStatus;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class WorkerImpl implements Worker {

    public static final WorkerImpl INSTANCE = new WorkerImpl();

    @Override
    public Future<Void> runJob(final JobWork work, final Transport transport, final Context context) {
        if (Status.isStopping(context)) {
            return CompletedFuture.INSTANCE;
        }
        if (Status.isComplete(context)) {
            return CompletedFuture.INSTANCE;
        }

        final RunJob run = new RunJob(this, work, context);
        final AfterJob after = new AfterJob(work, context);
        after.register(transport.wrapSynchronization(run));

        return transport.executeOnThisThread(
                run.fail(transport, new FailJob(context))
                        .always(transport, after
                                .fail(transport, new FailJob(context))
                        )
        );

        //try {
        //    work.before(context);
        //    final Future<?> job = transport.runJob(work, context);
        //} catch (final Exception e) {
        //    Status.failed(transport, context);
        //}
        //try {
        //    work.after(context);
        //    Status.postJob(transport, context);
        //} catch (final Exception e) {
        //    Status.failed(transport, context);
        //}
    }

    @Override
    public Future<Void> runExecution(final ExecutionWork work, final Transport transport, final Context context) throws Exception {
        if (Status.isStopping(context)) {
            return CompletedFuture.INSTANCE;
        }
        if (Status.isComplete(context)) {
            return CompletedFuture.INSTANCE;
        }

        final RunExecution run = new RunExecution(this, work, context);
        final AfterExecution after = new AfterExecution(this, work, context);
        after.register(transport.wrapSynchronization(run));

        return transport.executeOnAnyThread(
                run.fail(transport, new FailJob(context))
                        .always(transport, after
                                .fail(transport, new FailJob(context))
                        )
        );

        //try {
        //    work.before(transport, context);
        //    final Future<StepExecution[]> execution = transport.run(work, context);
        //    context.setCurrentExecutionResults(execution);
        //} catch (final Exception e) {
        //    Status.failed(transport, context);
        //}
        //try {
        //    work.after(transport, context);
        //} catch (final Exception e) {
        //    Status.failed(transport, context);
        //}
    }

    @Override
    public Future<Void> runPartition(final PartitionWork work, final Transport transport, final Context context) throws Exception {
        if (Status.isStopping(context)) {
            return CompletedFuture.INSTANCE;
        }
        if (Status.isComplete(context)) {
            return CompletedFuture.INSTANCE;
        }
        //try {
        //    transport.getPartitions(work, context);
        //} catch (final Exception e) {
        //    Status.failed(transport, context);
        //}
        return null;
    }

    @Override
    public ExecutionWork transitionOrSetStatus(final Transport transport, final Context context, final List<? extends TransitionWork> transitions, final String next) throws Exception {
        if (next != null) {
            return context.getJob().next(next);
        }
        final BatchStatus status = transport.getRepository().getJobExecution(context.getJobExecutionId()).getBatchStatus();
        for (final TransitionWork transition : transitions) {
            if (Status.matches(status, transition.getOn())) {
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
