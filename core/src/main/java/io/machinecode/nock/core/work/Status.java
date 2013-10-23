package io.machinecode.nock.core.work;

import io.machinecode.nock.core.util.Index;
import io.machinecode.nock.spi.Repository;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.work.TransitionWork.Result;

import javax.batch.operations.JobSecurityException;
import javax.batch.operations.NoSuchJobExecutionException;
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
        return context.getJobContext().getBatchStatus().equals(BatchStatus.STOPPING);
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

    public static boolean isComplete(final String exitStatus) {
        return matches(exitStatus, BatchStatus.COMPLETED)
                || matches(exitStatus, BatchStatus.FAILED)
                || matches(exitStatus, BatchStatus.STOPPED)
                || matches(exitStatus, BatchStatus.ABANDONED);
    }

    public static void finishJob(final Transport transport, final Context context, final Result result) throws Exception {
        finishJob(transport.getRepository(), context.getJobExecutionId(), result);
    }

    public static void finishJob(final Repository repository, final long jobExecutionId, final Result result) throws NoSuchJobExecutionException, JobSecurityException {
        repository.finishJobExecution(jobExecutionId, result.batchStatus, result.exitStatus, new Date());
    }

    public static void finishStep(final Repository repository, final long stepExecutionId, final BatchStatus batchStatus, final String exitStatus) throws NoSuchJobExecutionException, JobSecurityException {
        repository.finishStepExecution(stepExecutionId, batchStatus, exitStatus, new Date());
    }

    public static void startingJob(final Repository repository, final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        repository.updateJobExecution(jobExecutionId, BatchStatus.STARTING, new Date());
    }

    public static void startedJob(final Repository repository, final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        repository.startJobExecution(jobExecutionId, new Date());
    }

    public static void stoppingJob(final Repository repository, final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        repository.updateJobExecution(jobExecutionId, BatchStatus.STOPPING, new Date());
    }

    public static void stoppedJob(final Repository repository, final long jobExecutionId, final String exitStatus) throws NoSuchJobExecutionException, JobSecurityException {
        repository.finishJobExecution(jobExecutionId, BatchStatus.STOPPED, exitStatus, new Date());
    }

    public static void stoppedStep(final Repository repository, final long stepExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        repository.updateStepExecution(stepExecutionId, BatchStatus.STOPPED, new Date());
    }

    public static void failedJob(final Repository repository, final long jobExecutionId, final String exitStatus) {
        repository.finishJobExecution(jobExecutionId, BatchStatus.FAILED, exitStatus, new Date());
    }

    public static void completedJob(final Repository repository, final long jobExecutionId, final String exitStatus) throws Exception {
        repository.finishJobExecution(jobExecutionId, BatchStatus.COMPLETED, exitStatus, new Date());
    }

    public static void abandonedJob(final Repository repository, final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        //Uses update as the job cannot be running when this is called, therefore the exit status will already have been set
        repository.updateJobExecution(jobExecutionId, BatchStatus.ABANDONED, new Date());
    }

    public static void postJob(final Repository repository, final long jobExecutionId, BatchStatus batchStatus, final String exitStatus) throws Exception {
        final String status = exitStatus == null ? batchStatus.name() : exitStatus;
        if (isComplete(status)) { //TODO Should this be exit or batch status?
            completedJob(repository, jobExecutionId, exitStatus);
        } else {
            failedJob(repository, jobExecutionId, exitStatus);
        }
    }
}
