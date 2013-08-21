package io.machinecode.nock.core.factory.partition;

import io.machinecode.nock.core.descriptor.partition.AnalyserImpl;
import io.machinecode.nock.core.descriptor.partition.CollectorImpl;
import io.machinecode.nock.core.descriptor.partition.PartitionImpl;
import io.machinecode.nock.core.descriptor.partition.PlanImpl;
import io.machinecode.nock.core.descriptor.partition.ReducerImpl;
import io.machinecode.nock.core.expression.JobParameterContext;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.work.partition.AnalyserWork;
import io.machinecode.nock.core.work.partition.CollectorWork;
import io.machinecode.nock.core.work.partition.PartitionWork;
import io.machinecode.nock.core.work.partition.PlanWork;
import io.machinecode.nock.core.work.partition.ReducerWork;
import io.machinecode.nock.spi.element.partition.Partition;
import io.machinecode.nock.spi.element.partition.Plan;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PlanPartitionFactory implements ElementFactory<Partition<? extends Plan>, PartitionImpl<PlanImpl>, PartitionWork<PlanWork>> {

    public static final PlanPartitionFactory INSTANCE = new PlanPartitionFactory();

    @Override
    public PartitionImpl<PlanImpl> produceDescriptor(final Partition<? extends Plan> that, final JobPropertyContext context) {
        final CollectorImpl collector = that.getCollector() == null ? null : CollectorFactory.INSTANCE.produceDescriptor(that.getCollector(), context);
        final AnalyserImpl analyser = that.getAnalyzer() == null ? null : AnalyserFactory.INSTANCE.produceDescriptor(that.getAnalyzer(), context);
        final ReducerImpl reducer = that.getReducer() == null ? null : ReducerFactory.INSTANCE.produceDescriptor(that.getReducer(), context);
        final PlanImpl strategy = that.getStrategy() == null ? null : PlanFactory.INSTANCE.produceDescriptor(that.getStrategy(), context);
        return new PartitionImpl<PlanImpl>(
                collector,
                analyser,
                reducer,
                strategy
        );
    }

    @Override
    public PartitionWork<PlanWork> produceExecution(final PartitionImpl<PlanImpl> that, final JobParameterContext context) {
        final CollectorWork collector = that.getCollector() == null ? null : CollectorFactory.INSTANCE.produceExecution(that.getCollector(), context);
        final AnalyserWork analyser = that.getAnalyzer() == null ? null : AnalyserFactory.INSTANCE.produceExecution(that.getAnalyzer(), context);
        final ReducerWork reducer = that.getReducer() == null ? null : ReducerFactory.INSTANCE.produceExecution(that.getReducer(), context);
        final PlanWork strategy = that.getStrategy() == null ? null : PlanFactory.INSTANCE.produceExecution(that.getStrategy(), context);
        return new PartitionWork<PlanWork>(
                collector,
                analyser,
                reducer,
                strategy
        );
    }

    @Override
    public PartitionWork<PlanWork> producePartitioned(final PartitionWork<PlanWork> that, final PartitionPropertyContext context) {
        final CollectorWork collector = that.getCollector() == null ? null : CollectorFactory.INSTANCE.producePartitioned(that.getCollector(), context);
        final AnalyserWork analyser = that.getAnalyzer() == null ? null : AnalyserFactory.INSTANCE.producePartitioned(that.getAnalyzer(), context);
        final ReducerWork reducer = that.getReducer() == null ? null : ReducerFactory.INSTANCE.producePartitioned(that.getReducer(), context);
        final PlanWork strategy = that.getStrategy() == null ? null : PlanFactory.INSTANCE.producePartitioned(that.getStrategy(), context);
        return new PartitionWork<PlanWork>(
                collector,
                analyser,
                reducer,
                strategy
        );
    }
}
