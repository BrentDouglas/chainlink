package io.machinecode.nock.core.factory.partition;

import io.machinecode.nock.core.descriptor.PropertiesImpl;
import io.machinecode.nock.core.descriptor.partition.AnalyserImpl;
import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobParameterContext;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.factory.PropertiesFactory;
import io.machinecode.nock.core.inject.ResolvableReference;
import io.machinecode.nock.core.work.partition.AnalyserWork;
import io.machinecode.nock.spi.element.partition.Analyser;

import javax.batch.api.partition.PartitionAnalyzer;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class AnalyserFactory implements ElementFactory<Analyser, AnalyserImpl, AnalyserWork> {

    public static final AnalyserFactory INSTANCE = new AnalyserFactory();

    @Override
    public AnalyserImpl produceDescriptor(final Analyser that, final JobPropertyContext context) {
        final String ref = Expression.resolveDescriptorProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceDescriptor(that.getProperties(), context);
        return new AnalyserImpl(ref, properties);
    }

    @Override
    public AnalyserWork produceExecution(final AnalyserImpl that, final JobParameterContext context) {
        final String ref = Expression.resolveExecutionProperty(that.getRef(), context);
        return new AnalyserWork(new ResolvableReference<PartitionAnalyzer>(ref, PartitionAnalyzer.class));
    }

    @Override
    public AnalyserWork producePartitioned(final AnalyserWork that, final PartitionPropertyContext context) {
        final String ref = Expression.resolvePartitionProperty(that.getRef(), context);
        return new AnalyserWork(new ResolvableReference<PartitionAnalyzer>(ref, PartitionAnalyzer.class));
    }
}
