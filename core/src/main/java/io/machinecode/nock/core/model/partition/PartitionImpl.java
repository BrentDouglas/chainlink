package io.machinecode.nock.core.model.partition;

import io.machinecode.nock.core.expression.PropertyContextImpl;
import io.machinecode.nock.core.impl.ExecutionContextImpl;
import io.machinecode.nock.core.impl.JobContextImpl;
import io.machinecode.nock.core.impl.StepContextImpl;
import io.machinecode.nock.core.work.ItemImpl;
import io.machinecode.nock.core.work.TaskExecutable;
import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.element.partition.Partition;
import io.machinecode.nock.spi.execution.CallbackExecutable;
import io.machinecode.nock.spi.execution.Executable;
import io.machinecode.nock.spi.execution.Executor;
import io.machinecode.nock.spi.execution.Item;
import io.machinecode.nock.spi.util.Messages;
import io.machinecode.nock.spi.work.PartitionTarget;
import io.machinecode.nock.spi.work.PartitionWork;
import io.machinecode.nock.spi.work.StrategyWork;
import io.machinecode.nock.spi.work.TaskWork;
import org.jboss.logging.Logger;

import javax.batch.api.partition.PartitionAnalyzer;
import javax.batch.api.partition.PartitionCollector;
import javax.batch.api.partition.PartitionPlan;
import javax.batch.api.partition.PartitionReducer;
import javax.batch.api.partition.PartitionReducer.PartitionStatus;
import javax.batch.runtime.BatchStatus;
import javax.transaction.TransactionManager;
import java.util.List;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PartitionImpl<T extends StrategyWork> implements Partition<T>, PartitionWork<T> {

    private static final Logger log = Logger.getLogger(PartitionImpl.class);

    private final CollectorImpl collector;
    private final AnalyserImpl analyser;
    private final ReducerImpl reducer;
    private final T strategy;

    private transient ThreadLocal<PartitionCollector> _collector;
    private transient PartitionReducer _reducer;
    private transient PartitionPlan _plan;
    private transient PartitionAnalyzer _analyser;

    public PartitionImpl(final CollectorImpl collector, final AnalyserImpl analyser, final ReducerImpl reducer, final T strategy) {
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

    public PartitionCollector loadPartitionCollector(final Executor executor, final ExecutionContext context) throws Exception {
        if (collector == null) {
            return null;
        }
        synchronized (collector) {
            if (_collector == null) {
                _collector = new ThreadLocal<PartitionCollector>();
            }
            if (_collector.get() == null) {
                _collector.set(collector.load(executor, context));
            }
        }
        return _collector.get();
    }

    public PartitionReducer loadPartitionReducer(final Executor executor, final ExecutionContext context) throws Exception {
        if (reducer == null) {
            return null;
        }
        if (_reducer == null) {
            _reducer = reducer.load(executor, context);
        }
        return _reducer;
    }

    public PartitionPlan loadPartitionPlan(final Executor executor, final ExecutionContext context) throws Exception {
        if (strategy == null) {
            return null;
        }
        if (_plan == null) {
            _plan = strategy.getPartitionPlan(executor, context);
        }
        return _plan;
    }

    public PartitionAnalyzer loadPartitionAnalyzer(final Executor executor, final ExecutionContext context) throws Exception {
        if (analyser == null) {
            return null;
        }
        if (_analyser == null) {
            _analyser = analyser.load(executor, context);
        }
        return _analyser;
    }

    // Lifecycle

    @Override
    public PartitionTarget map(final TaskWork task, final Executor executor, final CallbackExecutable thisExecutable, final ExecutionContext context, final int timeout) throws Exception {
        final long jobExecutionId = context.getJobExecutionId();
        final PartitionReducer reducer = this.loadPartitionReducer(executor, context);
        if (reducer != null) {
            log.debugf(Messages.get("partition.before.partitioned.step"), jobExecutionId);
            reducer.beginPartitionedStep();
        }
        final PartitionPlan plan = loadPartitionPlan(executor, context);
        final int partitions = plan.getPartitions();
        final Properties[] properties = plan.getPartitionProperties();
        //if (partitions != properties.length) {
        //    throw new IllegalStateException(Messages.format("partition.properties.length", jobExecutionId, partitions, properties.length));
        //}
        final String id = context.getStepContext().getStepName();
        final Executable[] executables = new Executable[partitions];
        for (int i = 0; i < partitions; ++i) {
            //TODO Not really sure if this is how properties are meant to be distributed
            final ExecutionContext partitionContext = new ExecutionContextImpl(
                    context.getJob(),
                    new JobContextImpl(context.getJobContext()),
                    new StepContextImpl(context.getStepContext()),
                    context.getJobExecution()
            );
            executables[i] = new TaskExecutable(
                    thisExecutable,
                    task.partition(new PropertyContextImpl(i < properties.length ? properties[i] : null)),
                    partitionContext,
                    id,
                    i,
                    timeout
            );
        }
        return new PartitionTarget(executables, plan.getThreads());
    }

    @Override
    public Item collect(final TaskWork task, final Executor executor, final ExecutionContext context, final BatchStatus batchStatus, final String exitStatus) throws Exception {
        final PartitionCollector collector = loadPartitionCollector(executor, context);
        if (collector != null) {
            log.debugf(Messages.get("partition.collect.partitioned.data"), context.getJobExecutionId(), this.collector.getRef());
            return new ItemImpl(
                    collector.collectPartitionData(),
                    batchStatus,
                    exitStatus
            );
        }
        return null;
    }

    @Override
    public void analyse(final TaskWork task, final Executor executor, final ExecutionContext context, final int timeout, final List<Item> items) throws Exception {
        final long jobExecutionId = context.getJobExecutionId();
        final PartitionAnalyzer analyzer = loadPartitionAnalyzer(executor, context);
        final PartitionReducer reducer = loadPartitionReducer(executor, context);
        final TransactionManager transactionManager = executor.getTransactionManager();
        PartitionStatus partitionStatus = PartitionStatus.COMMIT;
        log.debugf(Messages.get("partition.set.transaction.timeout"), jobExecutionId, timeout);
        transactionManager.setTransactionTimeout(timeout);
        transactionManager.begin();
        try {
            if (analyzer != null) {
                if (this.collector != null) {
                    for (final Item that : items) {
                        log.debugf(Messages.get("partition.analyse.collector.data"), jobExecutionId, this.analyser.getRef());
                        analyzer.analyzeCollectorData(that.getData());
                    }
                }
                for (final Item item : items) {
                    analyzer.analyzeStatus(item.getBatchStatus(), item.getExitStatus());
                }
            }
            if (reducer != null) {
                log.debugf(Messages.get("partition.before.partitioned.step.complete"), jobExecutionId, this.reducer.getRef());
                reducer.beforePartitionedStepCompletion();
            }
            transactionManager.commit();
        } catch (final Exception e) {
            log.infof(e, Messages.get("partition.caught.while.analysing"), jobExecutionId);
            partitionStatus = PartitionStatus.ROLLBACK;
            transactionManager.setRollbackOnly();
            if (reducer != null) {
                log.debugf(Messages.get("partition.rollback.partitioned.step"), jobExecutionId, this.reducer.getRef());
                reducer.rollbackPartitionedStep();
            }
            transactionManager.rollback();
        } finally {
            if (reducer != null) {
                log.debugf(Messages.get("partition.after.partitioned.step"), jobExecutionId, this.reducer.getRef(), partitionStatus);
                reducer.afterPartitionedStepCompletion(partitionStatus);
            }
        }
    }
}
