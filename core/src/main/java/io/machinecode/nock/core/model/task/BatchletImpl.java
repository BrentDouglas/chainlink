package io.machinecode.nock.core.model.task;

import io.machinecode.nock.core.factory.task.BatchletFactory;
import io.machinecode.nock.core.loader.TypedArtifactReference;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.PropertyReferenceImpl;
import io.machinecode.nock.core.model.partition.PartitionImpl;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.element.task.Batchlet;
import io.machinecode.nock.spi.factory.PropertyContext;
import io.machinecode.nock.spi.inject.InjectionContext;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.work.TaskWork;
import io.machinecode.nock.spi.work.Worker;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class BatchletImpl extends PropertyReferenceImpl<javax.batch.api.Batchlet> implements Batchlet, TaskWork {

    private final PartitionImpl<?> partition;
    private transient javax.batch.api.Batchlet batchlet;

    private transient volatile boolean stopping = false;

    public BatchletImpl(final String ref, final PropertiesImpl properties, final PartitionImpl<?> partition) {
        super(new TypedArtifactReference<javax.batch.api.Batchlet>(ref, javax.batch.api.Batchlet.class), properties);
        this.partition = partition;
    }

    @Override
    public void run(final Worker worker, final Transport transport, final Context context) throws Exception {
        final InjectionContext injectionContext = transport.createInjectionContext(context);
        synchronized (this) {
            if (stopping) {
                return;
            }
            batchlet = load(injectionContext);
        }
        batchlet.process();
        if (partition != null) {
            partition.collect(this, worker, transport, context);
        }
    }

    @Override
    public synchronized void stop() throws Exception {
        stopping = true;
        if (batchlet != null) {
            batchlet.stop();
        }
        //TODO Mark job and step batch status STOPPED
    }

    @Override
    public TaskWork partition(final PropertyContext context) {
        return BatchletFactory.INSTANCE.producePartitioned(this, null, this.partition, context);
    }
}
