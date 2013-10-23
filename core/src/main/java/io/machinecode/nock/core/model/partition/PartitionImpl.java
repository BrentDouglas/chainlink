package io.machinecode.nock.core.model.partition;

import io.machinecode.nock.core.expression.PropertyContextImpl;
import io.machinecode.nock.core.work.task.RunTask;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.element.partition.Partition;
import io.machinecode.nock.spi.inject.InjectionContext;
import io.machinecode.nock.spi.transport.Executable;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.work.Bucket;
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
import javax.batch.runtime.context.StepContext;
import javax.transaction.TransactionManager;
import java.io.Serializable;
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

    public PartitionCollector loadPartitionCollector(final InjectionContext injectionContext) {
        if (collector == null) {
            return null;
        }
        synchronized (collector) {
            if (_collector == null) {
                _collector = new ThreadLocal<PartitionCollector>();
            }
            if (_collector.get() == null) {
                _collector.set(collector.load(injectionContext));
            }
        }
        return _collector.get();
    }

    public PartitionReducer loadPartitionReducer(final InjectionContext injectionContext) {
        if (reducer == null) {
            return null;
        }
        if (_reducer == null) {
            _reducer = reducer.load(injectionContext);
        }
        return _reducer;
    }

    public PartitionPlan loadPartitionPlan(final InjectionContext injectionContext) throws Exception {
        if (strategy == null) {
            return null;
        }
        if (_plan == null) {
            _plan = strategy.getPartitionPlan(injectionContext);
        }
        return _plan;
    }

    public PartitionAnalyzer loadPartitionAnalyzer(final InjectionContext injectionContext) {
        if (analyser == null) {
            return null;
        }
        if (_analyser == null) {
            _analyser = analyser.load(injectionContext);
        }
        return _analyser;
    }

    // Lifecycle

    @Override
    public PartitionTarget map(final TaskWork task, final Transport transport, final Context context, final int timeout) throws Exception {
        final InjectionContext injectionContext = transport.createInjectionContext(context);
        this.loadPartitionReducer(injectionContext).beginPartitionedStep();
        final PartitionPlan plan = loadPartitionPlan(injectionContext); //TODO Can throw
        final int partitions = plan.getPartitions();
        final Properties[] properties = plan.getPartitionProperties();
        if (partitions != properties.length) {
            throw new IllegalStateException(); //TODO Validation error of pass in empty props
        }
        transport.setBucket(
                new Bucket(
                        new Serializable[partitions]
                ),
                task
        );
        final Executable[] executables = new Executable[partitions];
        for (int i = 0; i < properties.length; ++i) {
            executables[i] = new RunTask(
                    task.partition(new PropertyContextImpl(properties[i])),
                    context,
                    timeout
            );
        }
        return new PartitionTarget(executables, plan.getThreads());
    }

    @Override
    public void collect(final TaskWork task, final Transport transport, final Context context) throws Exception {
        final InjectionContext injectionContext = transport.createInjectionContext(context);
        final PartitionCollector collector = loadPartitionCollector(injectionContext);
        if (collector != null) {
            transport.getBucket(task).give(collector.collectPartitionData());
        }
    }

    @Override
    public void analyse(final TaskWork task, final Transport transport, final Context context, final int timeout) throws Exception {
        final InjectionContext injectionContext = transport.createInjectionContext(context);
        final PartitionAnalyzer analyzer = loadPartitionAnalyzer(injectionContext);
        final PartitionReducer reducer = loadPartitionReducer(injectionContext);
        final StepContext stepContext = context.getStepContext();
        final TransactionManager transactionManager = transport.getTransactionManager();
        PartitionStatus partitionStatus = PartitionStatus.COMMIT;
        transactionManager.setTransactionTimeout(timeout);
        transactionManager.begin();
        try {
            if (analyzer != null) {
                final Bucket bucket = transport.evictBucket(task);
                for (final Serializable that : bucket.take()) {
                    analyzer.analyzeCollectorData(that);
                }
                analyzer.analyzeStatus(stepContext.getBatchStatus(), stepContext.getExitStatus());
            }
            if (reducer != null) {
                reducer.beforePartitionedStepCompletion();
            }
            transactionManager.commit();
        } catch (final Exception e) {
            log.debugf(e, "");
            partitionStatus = PartitionStatus.ROLLBACK;
            transactionManager.setRollbackOnly();
            if (reducer != null) {
                reducer.rollbackPartitionedStep();
            }
            transactionManager.rollback();
        } finally {
            if (reducer != null) {
                reducer.afterPartitionedStepCompletion(partitionStatus);
            }
        }
    }
}
