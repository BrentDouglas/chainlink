package io.machinecode.nock.core.work;

import io.machinecode.nock.spi.Checkpoint;
import io.machinecode.nock.spi.ExecutionRepository;
import io.machinecode.nock.spi.util.Messages;
import org.jboss.logging.Logger;

import javax.batch.operations.JobSecurityException;
import javax.batch.operations.NoSuchJobExecutionException;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.Metric;
import java.io.Serializable;
import java.util.Date;

/**
 * Wrapper to log calls to execution repository
 *
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class Repository {

    private static final Logger log = Logger.getLogger(Repository.class);

    public static void failedJob(final ExecutionRepository repository, final long jobExecutionId, final String exitStatus) {
        finishJob(repository, jobExecutionId, BatchStatus.FAILED, exitStatus);
    }

    public static void completedJob(final ExecutionRepository repository, final long jobExecutionId, final String exitStatus) throws Exception {
        finishJob(repository, jobExecutionId, BatchStatus.COMPLETED, exitStatus);
    }

    public static void finishJob(final ExecutionRepository repository, final long jobExecutionId, final BatchStatus batchStatus, final String exitStatus) throws NoSuchJobExecutionException, JobSecurityException {
        finishJob(repository, jobExecutionId, batchStatus, exitStatus, null);
    }

    public static void finishJob(final ExecutionRepository repository, final long jobExecutionId, final BatchStatus batchStatus, final String exitStatus, final String restartElementId) throws NoSuchJobExecutionException, JobSecurityException {
        final String es = exitStatus == null ? batchStatus.name() : exitStatus;
        log.debugf(Messages.get("NOCK-017000.repository.finish.job.with"), jobExecutionId, batchStatus, es, restartElementId);
        repository.finishJobExecution(jobExecutionId, batchStatus, es, restartElementId, new Date());
    }

    public static void finishStep(final ExecutionRepository repository, final long jobExecutionId, final long stepExecutionId, final BatchStatus batchStatus, final String exitStatus, final Metric[] metrics) throws NoSuchJobExecutionException, JobSecurityException {
        final String es = exitStatus == null ? batchStatus.name() : exitStatus;
        log.debugf(Messages.get("NOCK-017001.repository.finish.step.with"), jobExecutionId, stepExecutionId, batchStatus, es);
        repository.finishStepExecution(stepExecutionId, batchStatus, es, metrics, new Date());
    }

    public static void updateStep(final ExecutionRepository repository, final long jobExecutionId, final long stepExecutionId,  final Serializable serializable, final Metric[] metrics, final Checkpoint checkpoint) throws NoSuchJobExecutionException, JobSecurityException {
        log.debugf(Messages.get("NOCK-017004.repository.update.step.checkpoint"), jobExecutionId, stepExecutionId);
        repository.updateStepExecution(stepExecutionId, serializable, metrics, checkpoint, new Date());
    }

    public static void updateStep(final ExecutionRepository repository, final long jobExecutionId, final long stepExecutionId,  final Serializable serializable, final Metric[] metrics) throws NoSuchJobExecutionException, JobSecurityException {
        log.debugf(Messages.get("NOCK-017003.repository.update.step"), jobExecutionId, stepExecutionId);
        repository.updateStepExecution(stepExecutionId, serializable, metrics, new Date());
    }

    public static void startedJob(final ExecutionRepository repository, final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        log.debugf(Messages.get("NOCK-017005.repository.start.job"), jobExecutionId);
        repository.startJobExecution(jobExecutionId, new Date());
    }

    private static void _updateJob(final ExecutionRepository repository, final long jobExecutionId, final BatchStatus batchStatus) throws NoSuchJobExecutionException, JobSecurityException {
        log.debugf(Messages.get("NOCK-017002.repository.update.job.with"), jobExecutionId, batchStatus);
        repository.updateJobExecution(jobExecutionId, batchStatus, new Date());
    }

    public static void abandonedJob(final ExecutionRepository repository, final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        //Uses update as the job cannot be running when this is called, therefore the exit status will already have been set
        _updateJob(repository, jobExecutionId, BatchStatus.ABANDONED);
    }
}
