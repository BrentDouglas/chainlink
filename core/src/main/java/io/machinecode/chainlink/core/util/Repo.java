package io.machinecode.chainlink.core.util;

import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.registry.RepositoryId;
import io.machinecode.chainlink.spi.repository.Repository;
import org.jboss.logging.Logger;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.Metric;
import java.io.Serializable;
import java.util.Date;

/**
 * Wrapper to log calls to execution repository
 *
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class Repo {

    private static final Logger log = Logger.getLogger(Repo.class);

    public static Repository getRepository(final Configuration configuration, final RepositoryId id) throws Exception {
        final Repository ours = configuration.getRegistry().getRepository(id);
        if (ours != null) {
            return ours;
        }
        return configuration.getTransport().getRepository(id);
    }

    public static void failedJob(final Repository repository, final long jobExecutionId, final String exitStatus) throws Exception{
        finishJob(repository, jobExecutionId, BatchStatus.FAILED, exitStatus);
    }

    public static void completedJob(final Repository repository, final long jobExecutionId, final String exitStatus) throws Exception {
        finishJob(repository, jobExecutionId, BatchStatus.COMPLETED, exitStatus);
    }

    public static void finishJob(final Repository repository, final long jobExecutionId, final BatchStatus batchStatus, final String exitStatus) throws Exception {
        finishJob(repository, jobExecutionId, batchStatus, exitStatus, null);
    }

    public static void finishJob(final Repository repository, final long jobExecutionId, final BatchStatus batchStatus, final String exitStatus, final String restartElementId) throws Exception {
        final String es = exitStatus == null ? batchStatus.name() : exitStatus;
        log.debugf(Messages.get("CHAINLINK-017000.repository.finish.job.with"), jobExecutionId, batchStatus, es, restartElementId);
        repository.finishJobExecution(jobExecutionId, batchStatus, es, restartElementId, new Date());
    }

    public static void finishStep(final Repository repository, final long jobExecutionId, final long stepExecutionId, final Metric[] metrics, final BatchStatus batchStatus, final String exitStatus) throws Exception {
        final String es = exitStatus == null ? batchStatus.name() : exitStatus;
        log.debugf(Messages.get("CHAINLINK-017001.repository.finish.step.with"), jobExecutionId, stepExecutionId, batchStatus, es);
        repository.finishStepExecution(stepExecutionId, metrics, batchStatus, es, new Date());
    }

    public static void updateStep(final Repository repository, final long jobExecutionId, final long stepExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint) throws Exception {
        log.debugf(Messages.get("CHAINLINK-017004.repository.update.step.checkpoint"), jobExecutionId, stepExecutionId);
        repository.updateStepExecution(stepExecutionId, metrics, persistentUserData, readerCheckpoint, writerCheckpoint, new Date());
    }

    public static void updateStep(final Repository repository, final long jobExecutionId, final long stepExecutionId, final Metric[] metrics, final Serializable persistentUserData) throws Exception {
        log.debugf(Messages.get("CHAINLINK-017003.repository.update.step"), jobExecutionId, stepExecutionId);
        repository.updateStepExecution(stepExecutionId, metrics, persistentUserData, new Date());
    }

    public static void startedJob(final Repository repository, final long jobExecutionId) throws Exception {
        log.debugf(Messages.get("CHAINLINK-017005.repository.start.job"), jobExecutionId);
        repository.startJobExecution(jobExecutionId, new Date());
    }

    private static void _updateJob(final Repository repository, final long jobExecutionId, final BatchStatus batchStatus) throws Exception {
        log.debugf(Messages.get("CHAINLINK-017002.repository.update.job.with"), jobExecutionId, batchStatus);
        repository.updateJobExecution(jobExecutionId, batchStatus, new Date());
    }

    public static void abandonedJob(final Repository repository, final long jobExecutionId) throws Exception {
        //Uses update as the job cannot be running when this is called, therefore the exit status will already have been set
        _updateJob(repository, jobExecutionId, BatchStatus.ABANDONED);
    }
}
