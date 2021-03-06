/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @author tags. All rights reserved.
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
package io.machinecode.chainlink.core.jsl.impl.partition;

import io.machinecode.chainlink.core.context.ExecutionContextImpl;
import io.machinecode.chainlink.core.context.ItemImpl;
import io.machinecode.chainlink.core.context.JobContextImpl;
import io.machinecode.chainlink.core.context.StepContextImpl;
import io.machinecode.chainlink.core.expression.PartitionPropertyContext;
import io.machinecode.chainlink.core.jsl.impl.task.TaskWork;
import io.machinecode.chainlink.core.util.Repo;
import io.machinecode.chainlink.core.work.TaskExecutable;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.context.Item;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.jsl.partition.Partition;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.RepositoryId;
import io.machinecode.chainlink.spi.repository.PartitionExecution;
import io.machinecode.chainlink.spi.repository.Repository;
import org.jboss.logging.Logger;

import javax.batch.api.partition.PartitionPlan;
import javax.batch.api.partition.PartitionReducer.PartitionStatus;
import javax.batch.runtime.BatchStatus;
import javax.transaction.Status;
import javax.transaction.TransactionManager;
import java.io.Serializable;
import java.util.Date;
import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class PartitionImpl<T extends StrategyWork> implements Partition<T>, Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(PartitionImpl.class);

    private final CollectorImpl collector;
    private final AnalyserImpl analyser;
    private final ReducerImpl reducer;
    private final T strategy;

    public PartitionImpl(final CollectorImpl collector, final AnalyserImpl analyser, final ReducerImpl reducer, final T strategy) {
        assert strategy != null;
        this.collector = collector;
        this.analyser = analyser;
        this.reducer = reducer;
        this.strategy = strategy;
    }

    @Override
    public CollectorImpl getCollector() {
        return this.collector;
    }

    @Override
    public AnalyserImpl getAnalyzer() {
        return this.analyser;
    }

    @Override
    public ReducerImpl getReducer() {
         return this.reducer;
    }

    @Override
    public T getStrategy() {
        return this.strategy;
    }

    public PartitionTarget map(final Configuration configuration, final RepositoryId repositoryId, final TaskWork task, final ExecutableId callbackId, final ExecutionContextImpl context, final int timeout, final Long restartStepExecutionId) throws Exception {
        final PartitionPlan plan = strategy.getPartitionPlan(configuration, context);
        final boolean restarting = restartStepExecutionId != null;
        final boolean override = plan.getPartitionsOverride();
        if (this.reducer != null) {
            //TODO Find out why not
            //if (restarting && !override) {
            //    reducer.rollbackPartitionedStep();
            //}
            log.debugf(Messages.get("CHAINLINK-011300.partition.reducer.before.partitioned.step"), context);
            this.reducer.beginPartitionedStep(configuration, context);
        }
        final StepContextImpl stepContext = context.getStepContext();
        final Repository repository = Repo.getRepository(configuration, repositoryId);
        final Properties[] properties = plan.getPartitionProperties();
        final Executable[] executables;
        final long stepExecutionId = context.getStepExecutionId();
        if (restarting && !override) {
            final PartitionExecution[] partitionExecutions = repository.getUnfinishedPartitionExecutions(restartStepExecutionId);
            executables = new Executable[partitionExecutions.length];
            for (int id = 0; id < partitionExecutions.length; ++id) {
                final PartitionExecution execution = partitionExecutions[id];
                final int partitionId = execution.getPartitionId();
                final PartitionExecution partitionExecution = repository.createPartitionExecution(
                        stepExecutionId,
                        partitionId,
                        execution.getPartitionParameters(),
                        execution.getPersistentUserData(),
                        execution.getReaderCheckpoint(),
                        execution.getWriterCheckpoint(),
                        new Date()
                );
                //TODO Which step execution should this be linked to?
                final StepContextImpl partitionStepContext = new StepContextImpl(
                        stepContext,
                        execution.getMetrics(),
                        execution.getPersistentUserData()
                );
                final ExecutionContextImpl partitionContext = new ExecutionContextImpl(
                        new JobContextImpl(context.getJobContext()),
                        partitionStepContext,
                        context.getJobExecutionId(),
                        context.getRestartJobExecutionId(),
                        context.getRestartElementId(),
                        partitionExecution.getPartitionExecutionId()
                );
                final Properties props = partitionId >= properties.length ? null : properties[partitionId];
                executables[id] = new TaskExecutable(
                        callbackId,
                        task.partition(new PartitionPropertyContext(props)),
                        partitionContext,
                        repositoryId,
                        timeout
                );
            }
        } else {
            final int partitions = plan.getPartitions();
            executables = new Executable[partitions];
            for (int partitionId = 0; partitionId < partitions; ++partitionId) {
                final Properties partitionProperties = partitionId < properties.length ? properties[partitionId] : null;
                final PartitionExecution partitionExecution = repository.createPartitionExecution(
                        stepExecutionId,
                        partitionId,
                        partitionProperties,
                        null,
                        null,
                        null,
                        new Date()
                );
                final ExecutionContextImpl partitionContext = new ExecutionContextImpl(
                        new JobContextImpl(context.getJobContext()),
                        new StepContextImpl(context.getStepContext()),
                        context.getJobExecutionId(),
                        context.getRestartJobExecutionId(),
                        context.getRestartElementId(),
                        partitionExecution.getPartitionExecutionId()
                );
                //if (partitions != properties.length) {
                //    throw new IllegalStateException(Messages.format("CHAINLINK-011000.partition.properties.length", context, partitions, properties.length));
                //}
                //TODO Not really sure if this is how properties are meant to be distributed
                executables[partitionId] = new TaskExecutable(
                        callbackId,
                        task.partition(new PartitionPropertyContext(partitionProperties)),
                        partitionContext,
                        repositoryId,
                        timeout
                );
            }
        }
        return new PartitionTarget(
                executables,
                plan.getThreads()
        );
    }

    public Item collect(final Configuration configuration, final ExecutionContext context, final BatchStatus batchStatus, final String exitStatus) throws Exception {
        if (this.collector != null) {
            log.debugf(Messages.get("CHAINLINK-011100.partition.collect.partitioned.data"), context, this.collector.getRef());
            return new ItemImpl(
                    this.collector.collectPartitionData(configuration, context),
                    batchStatus,
                    exitStatus
            );
        }
        return new ItemImpl(null, batchStatus, exitStatus);
    }

    public void analyse(final Configuration configuration, final ExecutionContext context, final Item... items) throws Exception {
        if (items == null || this.analyser == null) {
            return;
        }
        final TransactionManager transactionManager = configuration.getTransactionManager();
        try {
            if (this.collector != null) { //TODO Is this right?
                for (final Item that : items) {
                    log.debugf(Messages.get("CHAINLINK-011200.partition.analyse.collector.data"), context, this.analyser.getRef());
                    this.analyser.analyzeCollectorData(configuration, context, that.getData());
                }
            }
            for (final Item item : items) {
                this.analyser.analyzeStatus(configuration, context, item.getBatchStatus(), item.getExitStatus());
            }
        } catch (final Exception e) {
            log.infof(e, Messages.get("CHAINLINK-011201.partition.analyser.caught"), context, this.analyser.getRef());
            if (transactionManager.getStatus() == Status.STATUS_ACTIVE) {
                transactionManager.setRollbackOnly();
            }
            throw e;
        }
    }

    public void reduce(final Configuration configuration, final PartitionStatus partitionStatus, final ExecutionContext context) throws Exception {
        final TransactionManager transactionManager = configuration.getTransactionManager();
        try {
            switch (partitionStatus) {
                case COMMIT:
                    try {
                        if (this.reducer != null) {
                            log.debugf(Messages.get("CHAINLINK-011301.partition.reducer.before.partitioned.step.complete"), context, this.reducer.getRef());
                            this.reducer.beforePartitionedStepCompletion(configuration, context);
                        }
                        transactionManager.commit();
                    } catch (final Exception e) {
                        log.warnf(e, Messages.get("CHAINLINK-011304.partition.reducer.caught"), context, this.reducer == null ? null : this.reducer.getRef());
                        transactionManager.rollback();
                    }
                    break;
                case ROLLBACK:
                    try {
                        if (this.reducer != null) {
                            log.debugf(Messages.get("CHAINLINK-011302.partition.reducer.rollback.partitioned.step"), context, this.reducer.getRef());
                            this.reducer.rollbackPartitionedStep(configuration, context);
                        }
                    } finally {
                        transactionManager.rollback();
                    }
                    break;
            }
        } finally {
            if (this.reducer != null) {
                log.debugf(Messages.get("CHAINLINK-011303.partition.reducer.after.partitioned.step"), context, this.reducer.getRef(), partitionStatus);
                this.reducer.afterPartitionedStepCompletion(configuration, context, partitionStatus);
            }
        }
    }
}
