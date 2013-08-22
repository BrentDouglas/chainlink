package io.machinecode.nock.core.factory.partition;

import io.machinecode.nock.core.model.partition.AnalyserImpl;
import io.machinecode.nock.core.model.partition.CollectorImpl;
import io.machinecode.nock.core.model.partition.PartitionImpl;
import io.machinecode.nock.core.model.partition.PlanImpl;
import io.machinecode.nock.core.model.partition.ReducerImpl;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.spi.element.partition.Partition;
import io.machinecode.nock.spi.element.partition.Plan;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PlanPartitionFactory implements ElementFactory<Partition<? extends Plan>, PartitionImpl<PlanImpl>> {

    public static final PlanPartitionFactory INSTANCE = new PlanPartitionFactory();

    @Override
    public PartitionImpl<PlanImpl> produceExecution(final Partition<? extends Plan> that, final JobPropertyContext context) {
        final CollectorImpl collector = that.getCollector() == null ? null : CollectorFactory.INSTANCE.produceExecution(that.getCollector(), context);
        final AnalyserImpl analyser = that.getAnalyzer() == null ? null : AnalyserFactory.INSTANCE.produceExecution(that.getAnalyzer(), context);
        final ReducerImpl reducer = that.getReducer() == null ? null : ReducerFactory.INSTANCE.produceExecution(that.getReducer(), context);
        final PlanImpl strategy = that.getStrategy() == null ? null : PlanFactory.INSTANCE.produceExecution(that.getStrategy(), context);
        return new PartitionImpl<PlanImpl>(
                collector,
                analyser,
                reducer,
                strategy
        );
    }

    @Override
    public PartitionImpl<PlanImpl> producePartitioned(final PartitionImpl<PlanImpl> that, final PartitionPropertyContext context) {
        final CollectorImpl collector = that.getCollector() == null ? null : CollectorFactory.INSTANCE.producePartitioned(that.getCollector(), context);
        final AnalyserImpl analyser = that.getAnalyzer() == null ? null : AnalyserFactory.INSTANCE.producePartitioned(that.getAnalyzer(), context);
        final ReducerImpl reducer = that.getReducer() == null ? null : ReducerFactory.INSTANCE.producePartitioned(that.getReducer(), context);
        final PlanImpl strategy = that.getStrategy() == null ? null : PlanFactory.INSTANCE.producePartitioned(that.getStrategy(), context);
        return new PartitionImpl<PlanImpl>(
                collector,
                analyser,
                reducer,
                strategy
        );
    }
}
