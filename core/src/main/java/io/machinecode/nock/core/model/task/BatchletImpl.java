package io.machinecode.nock.core.model.task;

import io.machinecode.nock.core.factory.task.BatchletFactory;
import io.machinecode.nock.core.loader.TypedArtifactReference;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.PropertyReferenceImpl;
import io.machinecode.nock.core.model.partition.PartitionImpl;
import io.machinecode.nock.core.deferred.DeferredImpl;
import io.machinecode.nock.core.work.RepositoryStatus;
import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.context.MutableStepContext;
import io.machinecode.nock.spi.element.task.Batchlet;
import io.machinecode.nock.spi.execution.Executor;
import io.machinecode.nock.spi.execution.Item;
import io.machinecode.nock.spi.factory.PropertyContext;
import io.machinecode.nock.spi.util.Messages;
import io.machinecode.nock.spi.deferred.Listener;
import io.machinecode.nock.spi.work.TaskWork;
import org.jboss.logging.Logger;

import javax.batch.operations.BatchRuntimeException;
import javax.batch.runtime.BatchStatus;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class BatchletImpl extends PropertyReferenceImpl<javax.batch.api.Batchlet> implements Batchlet, TaskWork {

    private static final Logger log = Logger.getLogger(BatchletImpl.class);

    private final PartitionImpl<?> partition;
    private transient javax.batch.api.Batchlet batchlet;

    private final DeferredImpl<ExecutionContext> delegate = new DeferredImpl<ExecutionContext>();

    public BatchletImpl(final TypedArtifactReference<javax.batch.api.Batchlet> ref, final PropertiesImpl properties, final PartitionImpl<?> partition) {
        super(ref, properties);
        this.partition = partition;
    }

    @Override
    public void run(final Executor executor, final ExecutionContext context, final int timeout) throws Exception {
        final long jobExecutionId = context.getJobExecutionId();
        final Long stepExecutionId = context.getStepExecutionId();
        final MutableStepContext stepContext = context.getStepContext();
        synchronized (this) {
            if (delegate.isCancelled()) {
                log.debugf(Messages.get("batchlet.cancelled"), jobExecutionId, stepExecutionId, getRef());
                return;
            }
            batchlet = load(executor, context);
        }
        BatchStatus batchStatus = BatchStatus.COMPLETED;
        String exitStatus = null;
        Throwable throwable = null;
        Item item = null;
        try {
            if (batchlet == null) {
                throw new IllegalStateException(getRef()); //TODO
            }
            log.debugf(Messages.get("batchlet.process"), jobExecutionId, stepExecutionId, getRef());
            exitStatus = batchlet.process();
            log.debugf(Messages.get("batchlet.status"), jobExecutionId, stepExecutionId, getRef(), exitStatus);
            resolve(null);
        } catch (final Throwable e) {
            stepContext.setBatchStatus(BatchStatus.FAILED);
            context.getJobContext().setBatchStatus(BatchStatus.FAILED);
            batchStatus = BatchStatus.FAILED;
            throwable = e;
        } finally {
            if (partition != null) {
                item = partition.collect(this, executor, context, batchStatus, exitStatus);
            } else {
                stepContext.setBatchletStatus(exitStatus);
            }
        }
        if (delegate.isCancelled()) {
            context.getStepContext().setBatchStatus(BatchStatus.STOPPED);
            context.getJobContext().setBatchStatus(BatchStatus.STOPPED);
            RepositoryStatus.stoppedJob(executor.getRepository(), jobExecutionId, context.getStepContext().getExitStatus());
        }
        if (item != null) {
            context.setItems(new Item[]{item});
        }
        //TODO These should be in a finally block
        if (throwable != null) {
            reject(throwable);
        } else {
            resolve(context);
        }
    }

    @Override
    public TaskWork partition(final PropertyContext context) {
        return BatchletFactory.INSTANCE.producePartitioned(this, null, this.partition, context);
    }

    @Override
    public boolean isPartitioned() {
        return partition != null;
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
    public boolean cancel(final boolean mayInterruptIfRunning) {
        final boolean ret = delegate.cancel(mayInterruptIfRunning);
        if (batchlet != null) {
            try {
                log.debugf(Messages.get("batchlet.stop"), getRef());
                batchlet.stop();
            } catch (final Exception e) {
                throw new BatchRuntimeException(Messages.format("batchlet.stop.exception", getRef()), e);
            }
        }
        return ret;
    }

    @Override
    public boolean isCancelled() {
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
}
