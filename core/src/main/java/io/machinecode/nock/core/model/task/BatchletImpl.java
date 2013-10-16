package io.machinecode.nock.core.model.task;

import io.machinecode.nock.core.TodoException;
import io.machinecode.nock.core.factory.task.BatchletFactory;
import io.machinecode.nock.core.loader.TypedArtifactReference;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.PropertyReferenceImpl;
import io.machinecode.nock.core.model.partition.PartitionImpl;
import io.machinecode.nock.core.work.DeferredImpl;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.element.task.Batchlet;
import io.machinecode.nock.spi.factory.PropertyContext;
import io.machinecode.nock.spi.inject.InjectionContext;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.work.TaskWork;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class BatchletImpl extends PropertyReferenceImpl<javax.batch.api.Batchlet> implements Batchlet, TaskWork {

    private final PartitionImpl<?> partition;
    private transient javax.batch.api.Batchlet batchlet;

    private transient volatile boolean stopping = false;

    private final DeferredImpl<Void> delegate = new DeferredImpl<Void>(); //TODO transience?

    public BatchletImpl(final String ref, final PropertiesImpl properties, final PartitionImpl<?> partition) {
        super(new TypedArtifactReference<javax.batch.api.Batchlet>(ref, javax.batch.api.Batchlet.class), properties);
        this.partition = partition;
    }

    @Override
    public String element() {
        return ELEMENT;
    }

    @Override
    public void run(final Transport transport, final Context context, final int timeout) throws Exception {
        final InjectionContext injectionContext = transport.createInjectionContext(context);
        synchronized (this) {
            if (delegate.isCancelled()) {
                return;
            }
            batchlet = load(injectionContext);
        }
        batchlet.process();
        if (partition != null) {
            partition.collect(this, transport, context);
        }
    }

    @Override
    public TaskWork partition(final PropertyContext context) {
        return BatchletFactory.INSTANCE.producePartitioned(this, null, this.partition, context);
    }

    @Override
    public void resolve(final Void that) {
        delegate.resolve(that);
    }

    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        final boolean ret = delegate.cancel(mayInterruptIfRunning);
        if (batchlet != null) {
            try {
                batchlet.stop();
            } catch (final Exception e) {
                throw new TodoException(e);
            }
        }
        //TODO Mark job and step batch status STOPPED
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
}
