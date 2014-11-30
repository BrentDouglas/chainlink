package io.machinecode.chainlink.core.factory.partition;

import io.machinecode.chainlink.core.element.partition.AnalyserImpl;
import io.machinecode.chainlink.core.element.partition.CollectorImpl;
import io.machinecode.chainlink.core.element.partition.PartitionImpl;
import io.machinecode.chainlink.core.element.partition.PlanImpl;
import io.machinecode.chainlink.core.element.partition.ReducerImpl;
import io.machinecode.chainlink.core.factory.ElementFactory;
import io.machinecode.chainlink.spi.element.partition.Partition;
import io.machinecode.chainlink.spi.element.partition.Plan;
import io.machinecode.chainlink.spi.expression.JobPropertyContext;
import io.machinecode.chainlink.spi.expression.PropertyContext;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
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
    public PartitionImpl<PlanImpl> producePartitioned(final PartitionImpl<PlanImpl> that, final PropertyContext context) {
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
