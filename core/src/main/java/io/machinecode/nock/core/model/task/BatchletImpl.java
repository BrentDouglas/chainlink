package io.machinecode.nock.core.model.task;

import io.machinecode.nock.core.factory.task.BatchletFactory;
import io.machinecode.nock.core.loader.TypedArtifactReference;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.PropertyReferenceImpl;
import io.machinecode.nock.core.model.partition.PartitionImpl;
import io.machinecode.nock.core.work.DeferredImpl;
import io.machinecode.nock.core.work.Status;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.context.MutableStepContext;
import io.machinecode.nock.spi.element.task.Batchlet;
import io.machinecode.nock.spi.factory.PropertyContext;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.util.Message;
import io.machinecode.nock.spi.work.Listener;
import io.machinecode.nock.spi.work.TaskWork;
import org.jboss.logging.Logger;

import javax.batch.operations.BatchRuntimeException;
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

    private final DeferredImpl<Void> delegate = new DeferredImpl<Void>(); //TODO transience?

    public BatchletImpl(final TypedArtifactReference<javax.batch.api.Batchlet> ref, final PropertiesImpl properties, final PartitionImpl<?> partition) {
        super(ref, properties);
        this.partition = partition;
    }

    @Override
    public String element() {
        return ELEMENT;
    }

    @Override
    public void run(final Transport transport, final Context context, final int timeout) throws Exception {
        final long jobExecutionId = context.getJobExecutionId();
        synchronized (this) {
            if (delegate.isCancelled()) {
                log.debugf(Message.get("batchlet.cancelled"), jobExecutionId, getRef());
                return;
            }
            batchlet = load(transport, context);
        }
        final MutableStepContext stepContext = context.getStepContext();
        try {
            if (batchlet == null) {
                throw new IllegalStateException(getRef()); //TODO
            }
            log.debugf(Message.get("batchlet.process"), jobExecutionId, getRef());
            stepContext.setBatchletStatus(batchlet.process());
        } finally {
            if (partition != null) {
                partition.collect(this, transport, context);
            }
        }
        if (delegate.isCancelled()) {
            Status.stoppedJob(transport.getRepository(), jobExecutionId, context.getStepContext().getExitStatus());
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
    public void resolve(final Void that) {
        delegate.resolve(that);
    }

    @Override
    public void onResolve(final Listener listener) {
        delegate.onResolve(listener);
    }

    @Override
    public void onCancel(final Listener listener) {
        delegate.onCancel(listener);
    }

    @Override
    public void always(final Listener listener) {
        delegate.always(listener);
    }

    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        final boolean ret = delegate.cancel(mayInterruptIfRunning);
        if (batchlet != null) {
            try {
                log.debugf(Message.get("batchlet.stop"), getRef());
                batchlet.stop();
            } catch (final Exception e) {
                throw new BatchRuntimeException(Message.format("batchlet.stop.exception", getRef()), e);
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
    public Void get() throws InterruptedException, ExecutionException {
        return delegate.get();
    }

    @Override
    public Void get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return delegate.get(timeout, unit);
    }

    @Override
    public void enlist() {
        delegate.enlist();
    }

    @Override
    public void delist() {
        delegate.delist();
    }

    @Override
    public boolean available() {
        return delegate.available();
    }
}
