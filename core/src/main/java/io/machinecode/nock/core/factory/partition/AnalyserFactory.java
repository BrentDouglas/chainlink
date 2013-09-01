package io.machinecode.nock.core.factory.partition;

import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.partition.AnalyserImpl;
import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.spi.factory.ElementFactory;
import io.machinecode.nock.core.factory.PropertiesFactory;
import io.machinecode.nock.spi.element.partition.Analyser;
import io.machinecode.nock.spi.factory.JobPropertyContext;
import io.machinecode.nock.spi.factory.PropertyContext;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class AnalyserFactory implements ElementFactory<Analyser, AnalyserImpl> {

    public static final AnalyserFactory INSTANCE = new AnalyserFactory();

    @Override
    public AnalyserImpl produceExecution(final Analyser that, final JobPropertyContext context) {
        final String ref = Expression.resolveExecutionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceExecution(that.getProperties(), context);
        return new AnalyserImpl(ref, properties);
    }

    @Override
    public AnalyserImpl producePartitioned(final AnalyserImpl that, final PropertyContext context) {
        final String ref = Expression.resolvePartitionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.producePartitioned(that.getProperties(), context);
        return new AnalyserImpl(ref, properties);
    }
}
