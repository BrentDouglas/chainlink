package io.machinecode.nock.core.factory.partition;

import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.model.partition.AnalyserImpl;
import io.machinecode.nock.core.model.partition.CollectorImpl;
import io.machinecode.nock.core.model.partition.PlanImpl;
import io.machinecode.nock.core.model.partition.PlanPartitionImpl;
import io.machinecode.nock.core.model.partition.ReducerImpl;
import io.machinecode.nock.jsl.api.partition.Partition;
import io.machinecode.nock.jsl.api.partition.Plan;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PlanPartitionFactory implements ElementFactory<Partition<? extends Plan>, PlanPartitionImpl> {

    public static final PlanPartitionFactory INSTANCE = new PlanPartitionFactory();

    @Override
    public PlanPartitionImpl produceBuildTime(final Partition<? extends Plan> that, final JobPropertyContext context) {
        final CollectorImpl collector = that.getCollector() == null ? null : CollectorFactory.INSTANCE.produceBuildTime(that.getCollector(), context);
        final AnalyserImpl analyser = that.getAnalyzer() == null ? null : AnalyserFactory.INSTANCE.produceBuildTime(that.getAnalyzer(), context);
        final ReducerImpl reducer = that.getReducer() == null ? null : ReducerFactory.INSTANCE.produceBuildTime(that.getReducer(), context);
        final PlanImpl mapper = that.getStrategy() == null ? null : PlanFactory.INSTANCE.produceBuildTime(that.getStrategy(), context);
        return new PlanPartitionImpl(
                collector,
                analyser,
                reducer,
                mapper
        );
    }

    @Override
    public PlanPartitionImpl producePartitionTime(final Partition<? extends Plan> that, final PartitionPropertyContext context) {
        final CollectorImpl collector = that.getCollector() == null ? null : CollectorFactory.INSTANCE.producePartitionTime(that.getCollector(), context);
        final AnalyserImpl analyser = that.getAnalyzer() == null ? null : AnalyserFactory.INSTANCE.producePartitionTime(that.getAnalyzer(), context);
        final ReducerImpl reducer = that.getReducer() == null ? null : ReducerFactory.INSTANCE.producePartitionTime(that.getReducer(), context);
        final PlanImpl mapper = that.getStrategy() == null ? null : PlanFactory.INSTANCE.producePartitionTime(that.getStrategy(), context);
        return new PlanPartitionImpl(
                collector,
                analyser,
                reducer,
                mapper
        );
    }
}
