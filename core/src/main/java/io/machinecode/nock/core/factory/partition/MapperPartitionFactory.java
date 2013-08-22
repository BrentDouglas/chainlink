package io.machinecode.nock.core.factory.partition;

import io.machinecode.nock.core.model.partition.AnalyserImpl;
import io.machinecode.nock.core.model.partition.CollectorImpl;
import io.machinecode.nock.core.model.partition.MapperImpl;
import io.machinecode.nock.core.model.partition.PartitionImpl;
import io.machinecode.nock.core.model.partition.ReducerImpl;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.spi.element.partition.Mapper;
import io.machinecode.nock.spi.element.partition.Partition;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class MapperPartitionFactory implements ElementFactory<Partition<? extends Mapper>, PartitionImpl<MapperImpl>> {

    public static final MapperPartitionFactory INSTANCE = new MapperPartitionFactory();

    @Override
    public PartitionImpl<MapperImpl> produceExecution(final Partition<? extends Mapper> that, final JobPropertyContext context) {
        final CollectorImpl collector = that.getCollector() == null ? null : CollectorFactory.INSTANCE.produceExecution(that.getCollector(), context);
        final AnalyserImpl analyser = that.getAnalyzer() == null ? null : AnalyserFactory.INSTANCE.produceExecution(that.getAnalyzer(), context);
        final ReducerImpl reducer = that.getReducer() == null ? null : ReducerFactory.INSTANCE.produceExecution(that.getReducer(), context);
        final MapperImpl mapper = that.getStrategy() == null ? null : MapperFactory.INSTANCE.produceExecution(that.getStrategy(), context);
        return new PartitionImpl<MapperImpl>(
                collector,
                analyser,
                reducer,
                mapper
        );
    }

    @Override
    public PartitionImpl<MapperImpl> producePartitioned(final PartitionImpl<MapperImpl> that, final PartitionPropertyContext context) {
        final CollectorImpl collector = that.getCollector() == null ? null : CollectorFactory.INSTANCE.producePartitioned(that.getCollector(), context);
        final AnalyserImpl analyser = that.getAnalyzer() == null ? null : AnalyserFactory.INSTANCE.producePartitioned(that.getAnalyzer(), context);
        final ReducerImpl reducer = that.getReducer() == null ? null : ReducerFactory.INSTANCE.producePartitioned(that.getReducer(), context);
        final MapperImpl strategy = that.getStrategy() == null ? null : MapperFactory.INSTANCE.producePartitioned(that.getStrategy(), context);
        return new PartitionImpl<MapperImpl>(
                collector,
                analyser,
                reducer,
                strategy
        );
    }
}
