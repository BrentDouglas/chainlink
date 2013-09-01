package io.machinecode.nock.core.work;

import io.machinecode.nock.spi.Repository;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.work.TransitionWork.Result;

import javax.batch.operations.BatchRuntimeException;
import javax.batch.runtime.BatchStatus;
import java.util.Date;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class Status {

    public static boolean matches(final CharSequence reference, final CharSequence target) {
        final int length = reference.length();
        for (int i = 0; i < length; ++i) {
            //TODO
        }
        return true;
    }

    public static boolean matches(final BatchStatus reference, final CharSequence target) {
        return matches(reference.name(), target);
    }

    public static boolean isRunning(final BatchStatus status) {
        return !(status.equals(BatchStatus.COMPLETED)
                || status.equals(BatchStatus.FAILED)
                || status.equals(BatchStatus.STOPPED)
        );
    }

    public static boolean isStopping(final Context context) {
        return !context.getJobContext().getBatchStatus().equals(BatchStatus.STOPPING);
    }

    public static boolean isComplete(final Context context) {
        final BatchStatus status = context.getJobContext().getBatchStatus();
        return status.equals(BatchStatus.COMPLETED)
                || status.equals(BatchStatus.FAILED)
                || status.equals(BatchStatus.STOPPED);
    }

    public static void update(final Transport transport, final Context context, final Result result) throws Exception {
        final Repository repository = transport.getRepository();
        //context.setBatchStatus(result.batchStatus);
        context.getJobContext().setExitStatus(result.exitStatus);
        repository.finishJobExecution(context.getJobExecutionId(), result.batchStatus, result.exitStatus, new Date());
    }

    public static void starting(final Transport transport, final Context context) throws Exception {
        final Repository repository = transport.getRepository();
        repository.updateJobExecution(context.getJobExecutionId(), BatchStatus.STARTING, new Date());
    }

    public static void started(final Transport transport, final Context context) throws Exception {
        final Repository repository = transport.getRepository();
        //context.setBatchStatus(BatchStatus.STARTED);
        repository.startJobExecution(context.getJobExecutionId(), BatchStatus.STARTED, new Date());
    }

    public static void stopping(final Transport transport, final Context context) throws Exception {
        final Repository repository = transport.getRepository();
        //context.setBatchStatus(BatchStatus.STOPPING);
        repository.updateJobExecution(context.getJobExecutionId(), BatchStatus.STOPPING, new Date());
    }

    public static void stopped(final Transport transport, final Context context) throws Exception {
        final Repository repository = transport.getRepository();
        //context.setBatchStatus(BatchStatus.STOPPED);
        repository.finishJobExecution(context.getJobExecutionId(), BatchStatus.STOPPED, new Date());
    }

    public static void failed(final Transport transport, final Context context) {
        final Repository repository;
        try {
            repository = transport.getRepository();
        } catch (final Exception f) {
            //If we cant reach the repo here, there's not much can be done about it as were already failing
            throw new BatchRuntimeException(f);
        }
        //context.setBatchStatus(BatchStatus.FAILED);
        repository.finishJobExecution(context.getJobExecutionId(), BatchStatus.FAILED, new Date());
    }

    public static void completed(final Transport transport, final Context context) throws Exception {
        final Repository repository = transport.getRepository();
        //context.setBatchStatus(BatchStatus.COMPLETED);
        repository.finishJobExecution(context.getJobExecutionId(), BatchStatus.COMPLETED, new Date());
    }

    public static void postJob(final Transport transport, final Context context) throws Exception {
        //TODO Test exit status and set failed
        if (isComplete(context)) {
            completed(transport, context);
        }
    }
}
