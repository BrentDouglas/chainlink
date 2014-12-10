package io.machinecode.chainlink.core.element.task;

import io.machinecode.chainlink.core.context.ItemImpl;
import io.machinecode.chainlink.core.element.PropertiesImpl;
import io.machinecode.chainlink.core.element.PropertyReferenceImpl;
import io.machinecode.chainlink.core.element.partition.PartitionImpl;
import io.machinecode.chainlink.core.factory.task.BatchletFactory;
import io.machinecode.chainlink.core.inject.ArtifactReferenceImpl;
import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.context.Item;
import io.machinecode.chainlink.spi.context.MutableStepContext;
import io.machinecode.chainlink.spi.element.task.Batchlet;
import io.machinecode.chainlink.spi.expression.PropertyContext;
import io.machinecode.chainlink.spi.inject.InjectablesProvider;
import io.machinecode.chainlink.spi.inject.InjectionContext;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.work.TaskWork;
import io.machinecode.then.api.Promise;
import org.jboss.logging.Logger;

import javax.batch.operations.BatchRuntimeException;
import javax.batch.runtime.BatchStatus;
import java.util.Date;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class BatchletImpl extends PropertyReferenceImpl<javax.batch.api.Batchlet> implements Batchlet, TaskWork {
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(BatchletImpl.class);

    private final PartitionImpl<?> partition;

    public BatchletImpl(final ArtifactReferenceImpl ref, final PropertiesImpl properties, final PartitionImpl<?> partition) {
        super(ref, properties);
        this.partition = partition;
    }

    @Override
    public void run(final RuntimeConfiguration configuration, final Promise<?,Throwable,?> promise, final ExecutionRepositoryId executionRepositoryId,
                    final ExecutionContext context, final int timeout) throws Throwable {
        final Long partitionExecutionId = context.getPartitionExecutionId();
        final MutableStepContext stepContext = context.getStepContext();
        final ExecutionRepository repository = configuration.getExecutionRepository(executionRepositoryId);
        stepContext.setBatchStatus(BatchStatus.STARTED);
        Throwable throwable = null;
        try {
            if (partitionExecutionId != null) {
                repository.startPartitionExecution(
                        partitionExecutionId,
                        new Date()
                );
            }
            synchronized (this) {
                if (promise.isCancelled()) {
                    log.debugf(Messages.get("CHAINLINK-013100.batchlet.cancelled"), context, getRef());
                    stepContext.setBatchStatus(BatchStatus.STOPPING);
                    if (partitionExecutionId != null) {
                        repository.finishPartitionExecution(
                                partitionExecutionId,
                                stepContext.getMetrics(),
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
                log.debugf(Messages.get("CHAINLINK-013101.batchlet.process"), context, getRef());
                final String exitStatus = this.process(configuration, context);
                log.debugf(Messages.get("CHAINLINK-013102.batchlet.status"), context, getRef(), exitStatus);
                if (stepContext.getExitStatus() == null) {
                    // TODO The RI doesn't set this until after running step listeners
                    stepContext.setExitStatus(exitStatus);
                }
            } catch (final Throwable e) {
                if (e instanceof Exception) {
                    stepContext.setException((Exception)e);
                }
                stepContext.setBatchStatus(BatchStatus.FAILED);
                throwable = e;
            }
            if (promise.isCancelled()) {
                if (stepContext.getBatchStatus() != BatchStatus.FAILED) {
                    stepContext.setBatchStatus(BatchStatus.STOPPING);
                }
            }
            final Item item;
            if (this.partition != null) {
                item = this.partition.collect(configuration, context, stepContext.getBatchStatus(), stepContext.getExitStatus());
            } else {
                item = new ItemImpl(null, stepContext.getBatchStatus(), stepContext.getExitStatus());
            }
            context.setItems(item);
            if (throwable != null) {
                throw throwable;
            }
        } finally {
            final BatchStatus batchStatus;
            if (promise.isCancelled()) {
                stepContext.setBatchStatus(batchStatus = BatchStatus.STOPPING);
            } else if (throwable != null) {
                batchStatus = BatchStatus.FAILED;
            } else {
                batchStatus = BatchStatus.COMPLETED;
            }
            if (partitionExecutionId != null) {
                repository.finishPartitionExecution(
                        partitionExecutionId,
                        stepContext.getMetrics(),
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
    public void cancel(final RuntimeConfiguration configuration, final ExecutionContext context) {
        try {
            log.debugf(Messages.get("CHAINLINK-013103.batchlet.stop"), context, getRef());
            this.stop(configuration, context);
        } catch (final Exception e) {
            throw new BatchRuntimeException(Messages.format("CHAINLINK-013000.batchlet.stop.exception", context, getRef()), e);
        }
        final MutableStepContext stepContext = context.getStepContext();
        if (stepContext != null) {
            stepContext.setBatchStatus(BatchStatus.STOPPING);
        }
    }

    public String process(final RuntimeConfiguration configuration, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            return load(javax.batch.api.Batchlet.class, configuration, context).process();
        } finally {
            provider.setInjectables(null);
        }
    }

    public void stop(final RuntimeConfiguration configuration, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(javax.batch.api.Batchlet.class, configuration, context).stop();
        } finally {
            provider.setInjectables(null);
        }
    }

    /*
    private class Delegate extends ChainImpl<ExecutionContext> {
        @Override
        protected String getResolveLogMessage() {
            return Messages.format("CHAINLINK-013200.batchlet.resolve", BatchletImpl.this._context, BatchletImpl.this.getRef());
        }

        @Override
        protected String getRejectLogMessage() {
            return Messages.format("CHAINLINK-013201.batchlet.reject", BatchletImpl.this._context, BatchletImpl.this.getRef());
        }

        @Override
        protected String getCancelLogMessage() {
            return Messages.format("CHAINLINK-013202.batchlet.cancel", BatchletImpl.this._context, BatchletImpl.this.getRef());
        }

        @Override
        protected String getTimeoutExceptionMessage() {
            return Messages.format("CHAINLINK-013001.batchlet.timeout", BatchletImpl.this._context, BatchletImpl.this.getRef());
        }
    }
    */
}
