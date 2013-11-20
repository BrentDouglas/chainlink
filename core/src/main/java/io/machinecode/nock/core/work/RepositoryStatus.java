package io.machinecode.nock.core.work;

import io.machinecode.nock.core.util.Index;
import io.machinecode.nock.spi.ExecutionRepository;
import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.util.Messages;
import org.jboss.logging.Logger;

import javax.batch.operations.JobSecurityException;
import javax.batch.operations.NoSuchJobExecutionException;
import javax.batch.runtime.BatchStatus;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class RepositoryStatus {

    private static final Logger log = Logger.getLogger(RepositoryStatus.class);

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

    public static boolean isStopping(final BatchStatus status) {
        return BatchStatus.STOPPING.equals(status);
    }

    public static boolean isStopping(final ExecutionContext context) {
        return BatchStatus.STOPPING.equals(context.getJobContext().getBatchStatus());
    }

    public static boolean isComplete(final ExecutionContext context) {
        return isComplete(context.getJobContext().getBatchStatus());
    }

    public static boolean isComplete(final BatchStatus status) {
        return BatchStatus.COMPLETED.equals(status)
                || BatchStatus.FAILED.equals(status)
                || BatchStatus.STOPPED.equals(status)
                || BatchStatus.ABANDONED.equals(status);
    }

    public static boolean isFailed(final BatchStatus status) {
        return BatchStatus.FAILED.equals(status);
    }

    public static void stoppedJob(final ExecutionRepository repository, final long jobExecutionId, final String exitStatus) throws NoSuchJobExecutionException, JobSecurityException {
        finishJob(repository, jobExecutionId, BatchStatus.STOPPED, exitStatus);
    }

    public static void failedJob(final ExecutionRepository repository, final long jobExecutionId, final String exitStatus) {
        finishJob(repository, jobExecutionId, BatchStatus.FAILED, exitStatus);
    }

    public static void completedJob(final ExecutionRepository repository, final long jobExecutionId, final String exitStatus) throws Exception {
        finishJob(repository, jobExecutionId, BatchStatus.COMPLETED, exitStatus);
    }

    public static void finishJob(final ExecutionRepository repository, final long jobExecutionId, final BatchStatus batchStatus, final String exitStatus) throws NoSuchJobExecutionException, JobSecurityException {
        finishJob(repository, jobExecutionId, batchStatus, exitStatus, null);
    }

    public static void finishJob(final ExecutionRepository repository, final long jobExecutionId, final BatchStatus batchStatus, final String exitStatus, final String restartId) throws NoSuchJobExecutionException, JobSecurityException {
        final String es = exitStatus == null ? batchStatus.name() : exitStatus;
        log.debugf(Messages.get("status.finish.job.with"), jobExecutionId, batchStatus, es);
        repository.finishJobExecution(jobExecutionId, batchStatus, es, restartId, new Date());
    }

    public static void finishStep(final ExecutionRepository repository, final long stepExecutionId, final BatchStatus batchStatus, final String exitStatus) throws NoSuchJobExecutionException, JobSecurityException {
        final String es = exitStatus == null ? batchStatus.name() : exitStatus;
        log.debugf(Messages.get("status.finish.step.with"), stepExecutionId, batchStatus, es);
        repository.finishStepExecution(stepExecutionId, batchStatus, es, new Date());
    }

    public static void startingJob(final ExecutionRepository repository, final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        _updateJob(repository, jobExecutionId, BatchStatus.STARTING);
    }

    public static void startedJob(final ExecutionRepository repository, final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        log.debugf(Messages.get("status.start.job"), jobExecutionId);
        repository.startJobExecution(jobExecutionId, new Date());
    }

    public static void stoppingJob(final ExecutionRepository repository, final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        _updateJob(repository, jobExecutionId, BatchStatus.STOPPING);
    }

    private static void _updateJob(final ExecutionRepository repository, final long jobExecutionId, final BatchStatus batchStatus) throws NoSuchJobExecutionException, JobSecurityException {
        log.debugf(Messages.get("status.update.job.with"), jobExecutionId, batchStatus);
        repository.updateJobExecution(jobExecutionId, batchStatus, new Date());
    }

    public static void stoppedStep(final ExecutionRepository repository, final long stepExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        log.debugf(Messages.get("status.update.step.with"), stepExecutionId, BatchStatus.STOPPED);
        repository.updateStepExecution(stepExecutionId, BatchStatus.STOPPED, new Date());
    }

    public static void abandonedJob(final ExecutionRepository repository, final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        //Uses update as the job cannot be running when this is called, therefore the exit status will already have been set
        _updateJob(repository, jobExecutionId, BatchStatus.ABANDONED);
    }
}
