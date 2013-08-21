package io.machinecode.nock.core.factory.partition;

import io.machinecode.nock.core.descriptor.partition.AnalyserImpl;
import io.machinecode.nock.core.descriptor.partition.CollectorImpl;
import io.machinecode.nock.core.descriptor.partition.MapperImpl;
import io.machinecode.nock.core.descriptor.partition.PartitionImpl;
import io.machinecode.nock.core.descriptor.partition.ReducerImpl;
import io.machinecode.nock.core.expression.JobParameterContext;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.work.partition.AnalyserWork;
import io.machinecode.nock.core.work.partition.CollectorWork;
import io.machinecode.nock.core.work.partition.MapperWork;
import io.machinecode.nock.core.work.partition.PartitionWork;
import io.machinecode.nock.core.work.partition.ReducerWork;
import io.machinecode.nock.spi.element.partition.Mapper;
import io.machinecode.nock.spi.element.partition.Partition;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class MapperPartitionFactory implements ElementFactory<Partition<? extends Mapper>, PartitionImpl<MapperImpl>, PartitionWork<MapperWork>> {

    public static final MapperPartitionFactory INSTANCE = new MapperPartitionFactory();

    @Override
    public PartitionImpl<MapperImpl> produceDescriptor(final Partition<? extends Mapper> that, final JobPropertyContext context) {
        final CollectorImpl collector = that.getCollector() == null ? null : CollectorFactory.INSTANCE.produceDescriptor(that.getCollector(), context);
        final AnalyserImpl analyser = that.getAnalyzer() == null ? null : AnalyserFactory.INSTANCE.produceDescriptor(that.getAnalyzer(), context);
        final ReducerImpl reducer = that.getReducer() == null ? null : ReducerFactory.INSTANCE.produceDescriptor(that.getReducer(), context);
        final MapperImpl mapper = that.getStrategy() == null ? null : MapperFactory.INSTANCE.produceDescriptor(that.getStrategy(), context);
        return new PartitionImpl<MapperImpl>(
                collector,
                analyser,
                reducer,
                mapper
        );
    }

    @Override
    public PartitionWork<MapperWork> produceExecution(final PartitionImpl<MapperImpl> that, final JobParameterContext context) {
        final CollectorWork collector = that.getCollector() == null ? null : CollectorFactory.INSTANCE.produceExecution(that.getCollector(), context);
        final AnalyserWork analyser = that.getAnalyzer() == null ? null : AnalyserFactory.INSTANCE.produceExecution(that.getAnalyzer(), context);
        final ReducerWork reducer = that.getReducer() == null ? null : ReducerFactory.INSTANCE.produceExecution(that.getReducer(), context);
        final MapperWork strategy = that.getStrategy() == null ? null : MapperFactory.INSTANCE.produceExecution(that.getStrategy(), context);
        return new PartitionWork<MapperWork>(
                collector,
                analyser,
                reducer,
                strategy
        );
    }

    @Override
    public PartitionWork<MapperWork> producePartitioned(final PartitionWork<MapperWork> that, final PartitionPropertyContext context) {
        final CollectorWork collector = that.getCollector() == null ? null : CollectorFactory.INSTANCE.producePartitioned(that.getCollector(), context);
        final AnalyserWork analyser = that.getAnalyzer() == null ? null : AnalyserFactory.INSTANCE.producePartitioned(that.getAnalyzer(), context);
        final ReducerWork reducer = that.getReducer() == null ? null : ReducerFactory.INSTANCE.producePartitioned(that.getReducer(), context);
        final MapperWork strategy = that.getStrategy() == null ? null : MapperFactory.INSTANCE.producePartitioned(that.getStrategy(), context);
        return new PartitionWork<MapperWork>(
                collector,
                analyser,
                reducer,
                strategy
        );
    }
}
