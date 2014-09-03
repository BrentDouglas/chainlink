package io.machinecode.chainlink.core.management;

import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.management.JobOperation;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.then.api.Promise;
import org.jboss.logging.Logger;

import javax.batch.operations.JobSecurityException;
import javax.batch.operations.NoSuchJobExecutionException;
import javax.batch.runtime.JobExecution;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class JobOperationImpl implements JobOperation {

    private static final Logger log = Logger.getLogger(JobOperationImpl.class);

    private final long jobExecutionId;
    private final Promise<?,?> promise;
    private final ExecutionRepository repository;

    public JobOperationImpl(final long jobExecutionId, final Promise<?,?> promise, final ExecutionRepository repository) {
        this.jobExecutionId = jobExecutionId;
        this.promise = promise;
        this.repository = repository;
    }

    @Override
    public long getJobExecutionId() {
        return jobExecutionId;
    }

    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        return promise != null && promise.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return promise != null && promise.isCancelled();
    }

    @Override
    public boolean isDone() {
        return promise == null || promise.isDone();
    }

    @Override
    public JobExecution get() throws InterruptedException, ExecutionException {
        if (promise != null) {
            promise.get();
        }
        try {
            final ExtendedJobExecution execution = repository.getJobExecution(jobExecutionId);
            log.tracef(Messages.get("CHAINLINK-033000.operation.get"), jobExecutionId, execution);
            return execution;
        } catch (final NoSuchJobExecutionException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

    @Override
    public JobExecution get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (promise != null) {
            promise.get(timeout, unit);
        }
        try {
            final ExtendedJobExecution execution = repository.getJobExecution(jobExecutionId);
            log.tracef(Messages.get("CHAINLINK-033000.operation.get"), jobExecutionId, execution);
            return execution;
        } catch (final NoSuchJobExecutionException e) {
            throw e;
        } catch (final JobSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }
}
