package io.machinecode.chainlink.core.util;

import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.util.Messages;
import org.jboss.logging.Logger;

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

    public static void failedJob(final ExecutionRepository repository, final long jobExecutionId, final String exitStatus) throws Exception{
        finishJob(repository, jobExecutionId, BatchStatus.FAILED, exitStatus);
    }

    public static void completedJob(final ExecutionRepository repository, final long jobExecutionId, final String exitStatus) throws Exception {
        finishJob(repository, jobExecutionId, BatchStatus.COMPLETED, exitStatus);
    }

    public static void finishJob(final ExecutionRepository repository, final long jobExecutionId, final BatchStatus batchStatus, final String exitStatus) throws Exception {
        finishJob(repository, jobExecutionId, batchStatus, exitStatus, null);
    }

    public static void finishJob(final ExecutionRepository repository, final long jobExecutionId, final BatchStatus batchStatus, final String exitStatus, final String restartElementId) throws Exception {
        final String es = exitStatus == null ? batchStatus.name() : exitStatus;
        log.debugf(Messages.get("CHAINLINK-017000.repository.finish.job.with"), jobExecutionId, batchStatus, es, restartElementId);
        repository.finishJobExecution(jobExecutionId, batchStatus, es, restartElementId, new Date());
    }

    public static void finishStep(final ExecutionRepository repository, final long jobExecutionId, final long stepExecutionId, final Metric[] metrics, final BatchStatus batchStatus, final String exitStatus) throws Exception {
        final String es = exitStatus == null ? batchStatus.name() : exitStatus;
        log.debugf(Messages.get("CHAINLINK-017001.repository.finish.step.with"), jobExecutionId, stepExecutionId, batchStatus, es);
        repository.finishStepExecution(stepExecutionId, metrics, batchStatus, es, new Date());
    }

    public static void updateStep(final ExecutionRepository repository, final long jobExecutionId, final long stepExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint) throws Exception {
        log.debugf(Messages.get("CHAINLINK-017004.repository.update.step.checkpoint"), jobExecutionId, stepExecutionId);
        repository.updateStepExecution(stepExecutionId, metrics, persistentUserData, readerCheckpoint, writerCheckpoint, new Date());
    }

    public static void updateStep(final ExecutionRepository repository, final long jobExecutionId, final long stepExecutionId, final Metric[] metrics, final Serializable persistentUserData) throws Exception {
        log.debugf(Messages.get("CHAINLINK-017003.repository.update.step"), jobExecutionId, stepExecutionId);
        repository.updateStepExecution(stepExecutionId, metrics, persistentUserData, new Date());
    }

    public static void startedJob(final ExecutionRepository repository, final long jobExecutionId) throws Exception {
        log.debugf(Messages.get("CHAINLINK-017005.repository.start.job"), jobExecutionId);
        repository.startJobExecution(jobExecutionId, new Date());
    }

    private static void _updateJob(final ExecutionRepository repository, final long jobExecutionId, final BatchStatus batchStatus) throws Exception {
        log.debugf(Messages.get("CHAINLINK-017002.repository.update.job.with"), jobExecutionId, batchStatus);
        repository.updateJobExecution(jobExecutionId, batchStatus, new Date());
    }

    public static void abandonedJob(final ExecutionRepository repository, final long jobExecutionId) throws Exception {
        //Uses update as the job cannot be running when this is called, therefore the exit status will already have been set
        _updateJob(repository, jobExecutionId, BatchStatus.ABANDONED);
    }
}
