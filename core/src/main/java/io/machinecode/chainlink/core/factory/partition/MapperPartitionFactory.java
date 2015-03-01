package io.machinecode.chainlink.core.factory.partition;

import io.machinecode.chainlink.core.expression.JobPropertyContext;
import io.machinecode.chainlink.core.expression.PropertyContext;
import io.machinecode.chainlink.core.jsl.impl.partition.AnalyserImpl;
import io.machinecode.chainlink.core.jsl.impl.partition.CollectorImpl;
import io.machinecode.chainlink.core.jsl.impl.partition.MapperImpl;
import io.machinecode.chainlink.core.jsl.impl.partition.PartitionImpl;
import io.machinecode.chainlink.core.jsl.impl.partition.ReducerImpl;
import io.machinecode.chainlink.spi.jsl.partition.Mapper;
import io.machinecode.chainlink.spi.jsl.partition.Partition;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class MapperPartitionFactory {

    public static PartitionImpl<MapperImpl> produceExecution(final Partition<? extends Mapper> that, final JobPropertyContext context) {
        final CollectorImpl collector = that.getCollector() == null ? null : CollectorFactory.produceExecution(that.getCollector(), context);
        final AnalyserImpl analyser = that.getAnalyzer() == null ? null : AnalyserFactory.produceExecution(that.getAnalyzer(), context);
        final ReducerImpl reducer = that.getReducer() == null ? null : ReducerFactory.produceExecution(that.getReducer(), context);
        final MapperImpl mapper = that.getStrategy() == null ? null : MapperFactory.produceExecution(that.getStrategy(), context);
        return new PartitionImpl<MapperImpl>(
                collector,
                analyser,
                reducer,
                mapper
        );
    }

    public static PartitionImpl<MapperImpl> producePartitioned(final PartitionImpl<MapperImpl> that, final PropertyContext context) {
        final CollectorImpl collector = that.getCollector() == null ? null : CollectorFactory.producePartitioned(that.getCollector(), context);
        final AnalyserImpl analyser = that.getAnalyzer() == null ? null : AnalyserFactory.producePartitioned(that.getAnalyzer(), context);
        final ReducerImpl reducer = that.getReducer() == null ? null : ReducerFactory.producePartitioned(that.getReducer(), context);
        final MapperImpl strategy = that.getStrategy() == null ? null : MapperFactory.producePartitioned(that.getStrategy(), context);
        return new PartitionImpl<MapperImpl>(
                collector,
                analyser,
                reducer,
                strategy
        );
    }
}
