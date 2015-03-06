package io.machinecode.chainlink.core.factory.partition;

import io.machinecode.chainlink.core.expression.JobPropertyContext;
import io.machinecode.chainlink.core.expression.PartitionPropertyContext;
import io.machinecode.chainlink.core.jsl.impl.partition.AnalyserImpl;
import io.machinecode.chainlink.core.jsl.impl.partition.CollectorImpl;
import io.machinecode.chainlink.core.jsl.impl.partition.PartitionImpl;
import io.machinecode.chainlink.core.jsl.impl.partition.PlanImpl;
import io.machinecode.chainlink.core.jsl.impl.partition.ReducerImpl;
import io.machinecode.chainlink.spi.jsl.partition.Partition;
import io.machinecode.chainlink.spi.jsl.partition.Plan;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class PlanPartitionFactory {

    public static PartitionImpl<PlanImpl> produceExecution(final Partition<? extends Plan> that, final JobPropertyContext context) {
        final CollectorImpl collector = that.getCollector() == null ? null : CollectorFactory.produceExecution(that.getCollector(), context);
        final AnalyserImpl analyser = that.getAnalyzer() == null ? null : AnalyserFactory.produceExecution(that.getAnalyzer(), context);
        final ReducerImpl reducer = that.getReducer() == null ? null : ReducerFactory.produceExecution(that.getReducer(), context);
        final PlanImpl strategy = that.getStrategy() == null ? null : PlanFactory.produceExecution(that.getStrategy(), context);
        return new PartitionImpl<PlanImpl>(
                collector,
                analyser,
                reducer,
                strategy
        );
    }

    public static PartitionImpl<PlanImpl> producePartitioned(final PartitionImpl<PlanImpl> that, final PartitionPropertyContext context) {
        final CollectorImpl collector = that.getCollector() == null ? null : CollectorFactory.producePartitioned(that.getCollector(), context);
        final AnalyserImpl analyser = that.getAnalyzer() == null ? null : AnalyserFactory.producePartitioned(that.getAnalyzer(), context);
        final ReducerImpl reducer = that.getReducer() == null ? null : ReducerFactory.producePartitioned(that.getReducer(), context);
        final PlanImpl strategy = that.getStrategy() == null ? null : PlanFactory.producePartitioned(that.getStrategy(), context);
        return new PartitionImpl<PlanImpl>(
                collector,
                analyser,
                reducer,
                strategy
        );
    }
}
