/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @authors tag. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.machinecode.chainlink.core.jsl.impl.task;

import io.machinecode.chainlink.core.context.ExecutionContextImpl;
import io.machinecode.chainlink.core.context.ItemImpl;
import io.machinecode.chainlink.core.context.StepContextImpl;
import io.machinecode.chainlink.core.expression.PartitionPropertyContext;
import io.machinecode.chainlink.core.factory.task.BatchletFactory;
import io.machinecode.chainlink.core.jsl.impl.PropertiesImpl;
import io.machinecode.chainlink.core.jsl.impl.PropertyReferenceImpl;
import io.machinecode.chainlink.core.jsl.impl.partition.PartitionImpl;
import io.machinecode.chainlink.core.util.Repo;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.context.Item;
import io.machinecode.chainlink.spi.inject.ArtifactReference;
import io.machinecode.chainlink.spi.inject.InjectablesProvider;
import io.machinecode.chainlink.spi.inject.InjectionContext;
import io.machinecode.chainlink.spi.jsl.task.Batchlet;
import io.machinecode.chainlink.spi.registry.RepositoryId;
import io.machinecode.chainlink.spi.repository.Repository;
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

    public BatchletImpl(final ArtifactReference ref, final PropertiesImpl properties, final PartitionImpl<?> partition) {
        super(ref, properties);
        this.partition = partition;
    }

    @Override
    public void run(final Configuration configuration, final Promise<?,Throwable,?> promise, final RepositoryId repositoryId,
                    final ExecutionContextImpl context, final int timeout) throws Throwable {
        final Long partitionExecutionId = context.getPartitionExecutionId();
        final StepContextImpl stepContext = context.getStepContext();
        final Repository repository = Repo.getRepository(configuration, repositoryId);
        stepContext.setBatchStatus(BatchStatus.STARTED);
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
        Throwable throwable = null;
        try {
            log.debugf(Messages.get("CHAINLINK-013101.batchlet.process"), context, getRef());
            final String exitStatus = this.process(configuration, context);
            log.debugf(Messages.get("CHAINLINK-013102.batchlet.status"), context, getRef(), exitStatus);
            if (stepContext.getExitStatus() == null) {
                // TODO The RI doesn't set this until after running step listeners
                stepContext.setExitStatus(exitStatus);
            }
        } catch (final Exception e) {
            stepContext.setException(e);
            stepContext.setBatchStatus(BatchStatus.FAILED);
            throwable = e;
        } catch (final Throwable e) {
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
        if (throwable != null) {
            throw throwable;
        }
    }

    @Override
    public TaskWork partition(final PartitionPropertyContext context) {
        return BatchletFactory.producePartitioned(this, this.partition, context);
    }

    @Override
    public void cancel(final Configuration configuration, final ExecutionContextImpl context) {
        try {
            log.debugf(Messages.get("CHAINLINK-013103.batchlet.stop"), context, getRef());
            this.stop(configuration, context);
        } catch (final Exception e) {
            throw new BatchRuntimeException(Messages.format("CHAINLINK-013000.batchlet.stop.exception", context, getRef()), e);
        }
        final StepContextImpl stepContext = context.getStepContext();
        assert stepContext != null; //TODO Message
        stepContext.setBatchStatus(BatchStatus.STOPPING);
    }

    public String process(final Configuration configuration, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            return load(javax.batch.api.Batchlet.class, configuration, context).process();
        } finally {
            provider.setInjectables(null);
        }
    }

    public void stop(final Configuration configuration, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(javax.batch.api.Batchlet.class, configuration, context).stop();
        } finally {
            provider.setInjectables(null);
        }
    }
}
