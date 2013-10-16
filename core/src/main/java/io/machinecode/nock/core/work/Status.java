package io.machinecode.nock.core.work;

import io.machinecode.nock.core.util.Index;
import io.machinecode.nock.spi.Repository;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.work.TransitionWork.Result;

import javax.batch.operations.BatchRuntimeException;
import javax.batch.runtime.BatchStatus;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class Status {

    /**
     * @see Pattern#quote(String)
     *
     * @param source Sequence to quote
     * @param start  Start index of the sequence to quote
     * @param end    End index of the sequence to quote
     * @param query  builder to accept the quoted pattern
     */
    private static void quote(final CharSequence source, final int start, final int end, final StringBuilder query) {
        int slashEIndex = Index.of(source, 0, end,  "\\E", 0, 2, start);
        if (slashEIndex == -1) {
            query.append("\\Q")
                    .append(source.subSequence(start, end))
                    .append("\\E");
            return;
        }

        query.append("\\Q");
        int current = 0;
        while ((slashEIndex = Index.of(source, 0, end,  "\\E", 0, 2, current)) != -1) {
            query.append(source.subSequence(current, slashEIndex));
            current = slashEIndex + 2;
            query.append("\\E\\\\E\\Q");
        }
        query.append(source.subSequence(current, source.length()));
        query.append("\\E");
    }

    private static int find(final CharSequence reference, int start, final int length) {
        for (int i = start; i < length; ++i) {
            switch (reference.charAt(i)) {
                case '*':
                case '?':
                    return i;
            }
        }
        return length;
    }

    public static boolean matches(final CharSequence reference, final CharSequence target) {
        final int rl = reference.length();

        final StringBuilder query = new StringBuilder();
        for (int ri = 0; ri < rl; ++ri) {
            final char r = reference.charAt(ri);
            switch (r) {
                case '*':
                    query.append(".*");
                    break;
                case '?':
                    query.append(".{1}");
                    break;
                default:
                    final int end = find(reference, ri + 1, rl);
                    quote(reference, ri, end, query);
                    ri = end - 1;
            }
        }
        return Pattern.compile(query.toString())
                .matcher(target)
                .matches();
    }

    public static boolean matches(final CharSequence reference, final BatchStatus target) {
        return matches(reference, target.name());
    }

    public static boolean isRunning(final BatchStatus status) {
        return !isComplete(status);
    }

    public static boolean isStopping(final Context context) {
        return !context.getJobContext().getBatchStatus().equals(BatchStatus.STOPPING);
    }

    public static boolean isComplete(final Context context) {
        return isComplete(context.getJobContext().getBatchStatus());
    }

    public static boolean isComplete(final BatchStatus status) {
        return status.equals(BatchStatus.COMPLETED)
                || status.equals(BatchStatus.FAILED)
                || status.equals(BatchStatus.STOPPED)
                || status.equals(BatchStatus.ABANDONED);
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
