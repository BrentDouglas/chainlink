package io.machinecode.chainlink.core.factory.partition;

import io.machinecode.chainlink.core.expression.JobPropertyContext;
import io.machinecode.chainlink.core.factory.ElementFactory;
import io.machinecode.chainlink.core.jsl.impl.partition.AnalyserImpl;
import io.machinecode.chainlink.core.jsl.impl.partition.CollectorImpl;
import io.machinecode.chainlink.core.jsl.impl.partition.PartitionImpl;
import io.machinecode.chainlink.core.jsl.impl.partition.PlanImpl;
import io.machinecode.chainlink.core.jsl.impl.partition.ReducerImpl;
import io.machinecode.chainlink.spi.expression.PropertyContext;
import io.machinecode.chainlink.spi.jsl.partition.Partition;
import io.machinecode.chainlink.spi.jsl.partition.Plan;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
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
