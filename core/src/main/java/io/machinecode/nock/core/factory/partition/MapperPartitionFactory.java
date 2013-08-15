package io.machinecode.nock.core.factory.partition;

import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.model.partition.AnalyserImpl;
import io.machinecode.nock.core.model.partition.CollectorImpl;
import io.machinecode.nock.core.model.partition.MapperImpl;
import io.machinecode.nock.core.model.partition.MapperPartitionImpl;
import io.machinecode.nock.core.model.partition.ReducerImpl;
import io.machinecode.nock.jsl.api.partition.Mapper;
import io.machinecode.nock.jsl.api.partition.Partition;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class MapperPartitionFactory implements ElementFactory<Partition<? extends Mapper>, MapperPartitionImpl> {

    public static final MapperPartitionFactory INSTANCE = new MapperPartitionFactory();

    @Override
    public MapperPartitionImpl produceBuildTime(final Partition<? extends Mapper> that, final JobPropertyContext context) {
        final CollectorImpl collector = that.getCollector() == null ? null : CollectorFactory.INSTANCE.produceBuildTime(that.getCollector(), context);
        final AnalyserImpl analyser = that.getAnalyzer() == null ? null : AnalyserFactory.INSTANCE.produceBuildTime(that.getAnalyzer(), context);
        final ReducerImpl reducer = that.getReducer() == null ? null : ReducerFactory.INSTANCE.produceBuildTime(that.getReducer(), context);
        final MapperImpl mapper = that.getStrategy() == null ? null : MapperFactory.INSTANCE.produceBuildTime(that.getStrategy(), context);
        return new MapperPartitionImpl(
                collector,
                analyser,
                reducer,
                mapper
        );
    }

    @Override
    public MapperPartitionImpl producePartitionTime(final Partition<? extends Mapper> that, final PartitionPropertyContext context) {
        final CollectorImpl collector = that.getCollector() == null ? null : CollectorFactory.INSTANCE.producePartitionTime(that.getCollector(), context);
        final AnalyserImpl analyser = that.getAnalyzer() == null ? null : AnalyserFactory.INSTANCE.producePartitionTime(that.getAnalyzer(), context);
        final ReducerImpl reducer = that.getReducer() == null ? null : ReducerFactory.INSTANCE.producePartitionTime(that.getReducer(), context);
        final MapperImpl mapper = that.getStrategy() == null ? null : MapperFactory.INSTANCE.producePartitionTime(that.getStrategy(), context);
        return new MapperPartitionImpl(
                collector,
                analyser,
                reducer,
                mapper
        );
    }
}
