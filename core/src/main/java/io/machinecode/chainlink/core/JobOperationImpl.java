package io.machinecode.chainlink.core;

import io.machinecode.chainlink.spi.ExecutionRepository;
import io.machinecode.chainlink.spi.JobOperation;
import io.machinecode.chainlink.spi.deferred.Deferred;

import javax.batch.runtime.JobExecution;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class JobOperationImpl implements JobOperation {
    private final long id;
    private final Deferred<?> deferred;
    private final ExecutionRepository repository;

    public JobOperationImpl(final long id, final Deferred<?> deferred, final ExecutionRepository repository) {
        this.id = id;
        this.deferred = deferred;
        this.repository = repository;
    }

    @Override
    public long getJobExecutionId() {
        return id;
    }

    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        return deferred != null && deferred.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return deferred != null && deferred.isCancelled();
    }

    @Override
    public boolean isDone() {
        return deferred == null || deferred.isDone();
    }

    @Override
    public JobExecution get() throws InterruptedException, ExecutionException {
        if (deferred != null) {
            deferred.get();
        }
        return repository.getJobExecution(id);
    }

    @Override
    public JobExecution get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (deferred != null) {
            deferred.get(timeout, unit);
        }
        return repository.getJobExecution(id);
    }
}
