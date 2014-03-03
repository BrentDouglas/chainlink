package io.machinecode.nock.core.model.task;

import io.machinecode.nock.core.factory.task.BatchletFactory;
import io.machinecode.nock.core.impl.InjectablesImpl;
import io.machinecode.nock.core.loader.ArtifactReferenceImpl;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.PropertyReferenceImpl;
import io.machinecode.nock.core.model.partition.PartitionImpl;
import io.machinecode.nock.core.deferred.DeferredImpl;
import io.machinecode.nock.core.work.ItemImpl;
import io.machinecode.nock.spi.ExecutionRepository;
import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.context.MutableStepContext;
import io.machinecode.nock.spi.element.task.Batchlet;
import io.machinecode.nock.spi.execution.Executor;
import io.machinecode.nock.spi.execution.Item;
import io.machinecode.nock.spi.factory.PropertyContext;
import io.machinecode.nock.spi.inject.InjectablesProvider;
import io.machinecode.nock.spi.inject.InjectionContext;
import io.machinecode.nock.spi.util.Messages;
import io.machinecode.nock.spi.deferred.Listener;
import io.machinecode.nock.spi.work.TaskWork;
import org.jboss.logging.Logger;

import javax.batch.operations.BatchRuntimeException;
import javax.batch.runtime.BatchStatus;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class BatchletImpl extends PropertyReferenceImpl<javax.batch.api.Batchlet> implements Batchlet, TaskWork {

    private static final Logger log = Logger.getLogger(BatchletImpl.class);

    private final PartitionImpl<?> partition;

    private final Delegate delegate = new Delegate();

    public BatchletImpl(final ArtifactReferenceImpl ref, final PropertiesImpl properties, final PartitionImpl<?> partition) {
        super(ref, properties);
        this.partition = partition;
    }

    // Lifecycle

    private transient volatile Executor _executor;
    private transient volatile ExecutionContext _context;

    @Override
    public void run(final Executor executor, final ExecutionContext context, final int timeout) throws Exception {
        synchronized (this) {
            this._context = context;
            this._executor = executor;
        }
        final Long stepExecutionId = context.getStepExecutionId();
        final Integer partitionId = context.getPartitionId();
        final MutableStepContext stepContext = context.getStepContext();
        final ExecutionRepository repository = executor.getRepository();
        stepContext.setBatchStatus(BatchStatus.STARTED);
        Throwable throwable = null;
        try {
            if (partitionId != null) {
                repository.updatePartitionExecution(
                        stepExecutionId,
                        partitionId,
                        stepContext.getPersistentUserData(),
                        BatchStatus.STARTED,
                        new Date()
                );
            }
            synchronized (this) {
                if (isCancelled()) {
                    log.debugf(Messages.get("NOCK-013100.batchlet.cancelled"), this._context, getRef());
                    stepContext.setBatchStatus(BatchStatus.STOPPING);
                    resolve(context);
                    if (partitionId != null) {
                        repository.finishPartitionExecution(
                                stepExecutionId,
                                partitionId,
                                stepContext.getPersistentUserData(),
                                BatchStatus.STOPPED,
                                stepContext.getExitStatus(),
                                new Date()
                        );
                    }
                    return;
                }
            }
            try {
                log.debugf(Messages.get("NOCK-013101.batchlet.process"), this._context, getRef());
                final String exitStatus = this.process(executor, context);
                log.debugf(Messages.get("NOCK-013102.batchlet.status"), this._context, getRef(), exitStatus);
                if (stepContext.getExitStatus() == null) {
                    // TODO Challenge why process even returns anything, should be void
                    // Also, the RI doesn't set this until after running step listeners
                    stepContext.setExitStatus(exitStatus);
                }
            } catch (final Throwable e) {
                if (e instanceof Exception) {
                    stepContext.setException((Exception)e);
                }
                stepContext.setBatchStatus(BatchStatus.FAILED);
                throwable = e;
            }
            if (isCancelled()) {
                if (stepContext.getBatchStatus() != BatchStatus.FAILED) {
                    stepContext.setBatchStatus(BatchStatus.STOPPING);
                }
            }
            final Item item;
            if (this.partition != null) {
                item = this.partition.collect(this, executor, context, stepContext.getBatchStatus(), stepContext.getExitStatus());
            } else {
                item = new ItemImpl(null, stepContext.getBatchStatus(), stepContext.getExitStatus());
            }
            context.setItems(item);
        } finally {
            final BatchStatus batchStatus;
            if (isCancelled()) {
                stepContext.setBatchStatus(batchStatus = BatchStatus.STOPPING);
            } else if (throwable != null) {
                batchStatus = BatchStatus.FAILED;
                reject(throwable);
            } else {
                batchStatus = BatchStatus.COMPLETED;
                resolve(context);
            }
            if (partitionId != null) {
                repository.finishPartitionExecution(
                        stepExecutionId,
                        partitionId,
                        stepContext.getPersistentUserData(),
                        batchStatus,
                        stepContext.getExitStatus(),
                        new Date()
                );
            }
        }
    }

    @Override
    public TaskWork partition(final PropertyContext context) {
        return BatchletFactory.INSTANCE.producePartitioned(this, null, this.partition, context);
    }

    @Override
    public void resolve(final ExecutionContext that) {
        delegate.resolve(that);
    }

    @Override
    public void reject(final Throwable that) {
        delegate.reject(that);
    }

    @Override
    public boolean isResolved() {
        return delegate.isResolved();
    }

    @Override
    public boolean isRejected() {
        return delegate.isRejected();
    }

    @Override
    public Throwable getFailure() throws InterruptedException, ExecutionException {
        return delegate.getFailure();
    }

    @Override
    public Throwable getFailure(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return delegate.getFailure(timeout, unit);
    }

    @Override
    public void onResolve(final Listener listener) {
        delegate.onResolve(listener);
    }

    @Override
    public void onReject(final Listener listener) {
        delegate.onReject(listener);
    }

    @Override
    public void onCancel(final Listener listener) {
        delegate.onCancel(listener);
    }

    @Override
    public void traverse(final Listener listener) {
        delegate.traverse(listener);
    }

    @Override
    public void await() throws InterruptedException, ExecutionException {
        delegate.await();
    }

    @Override
    public void await(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        delegate.await(timeout, unit);
    }

    @Override
    public synchronized boolean cancel(final boolean mayInterruptIfRunning) {
        final boolean set;
        synchronized (this) {
            set = this._context != null && this._executor != null;
        }
        if (set) {
            try {
                log.debugf(Messages.get("NOCK-013103.batchlet.stop"), this._context, getRef());
                this.stop(this._executor, this._context);
            } catch (final Exception e) {
                reject(new BatchRuntimeException(Messages.format("NOCK-013000.batchlet.stop.exception", this._context, getRef()), e));
            }
            final MutableStepContext stepContext = this._context.getStepContext();
            if (stepContext != null) {
                stepContext.setBatchStatus(BatchStatus.STOPPING);
            }
            notifyAll();
        }
        return delegate.cancel(mayInterruptIfRunning);
    }

    @Override
    public synchronized boolean isCancelled() {
        return delegate.isCancelled();
    }

    @Override
    public boolean isDone() {
        return delegate.isDone();
    }

    @Override
    public ExecutionContext get() throws InterruptedException, ExecutionException {
        return delegate.get();
    }

    @Override
    public ExecutionContext get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return delegate.get(timeout, unit);
    }

    public String process(final Executor executor, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            return load(javax.batch.api.Batchlet.class, injectionContext, context).process();
        } finally {
            provider.setInjectables(null);
        }
    }

    public void stop(final Executor executor, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(javax.batch.api.Batchlet.class, injectionContext, context).stop();
        } finally {
            provider.setInjectables(null);
        }
    }

    private class Delegate extends DeferredImpl<ExecutionContext> {
        @Override
        protected String getResolveLogMessage() {
            return Messages.format("NOCK-013200.batchlet.resolve", BatchletImpl.this._context, BatchletImpl.this.getRef());
        }

        @Override
        protected String getRejectLogMessage() {
            return Messages.format("NOCK-013201.batchlet.reject", BatchletImpl.this._context, BatchletImpl.this.getRef());
        }

        @Override
        protected String getCancelLogMessage() {
            return Messages.format("NOCK-013202.batchlet.cancel", BatchletImpl.this._context, BatchletImpl.this.getRef());
        }

        @Override
        protected String getTimeoutExceptionMessage() {
            return Messages.format("NOCK-013001.batchlet.timeout", BatchletImpl.this._context, BatchletImpl.this.getRef());
        }
    }
}
