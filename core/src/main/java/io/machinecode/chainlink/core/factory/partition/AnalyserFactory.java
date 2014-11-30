package io.machinecode.chainlink.core.factory.partition;

import io.machinecode.chainlink.core.element.PropertiesImpl;
import io.machinecode.chainlink.core.element.partition.AnalyserImpl;
import io.machinecode.chainlink.core.expression.Expression;
import io.machinecode.chainlink.core.factory.ElementFactory;
import io.machinecode.chainlink.core.factory.PropertiesFactory;
import io.machinecode.chainlink.core.inject.ArtifactReferenceImpl;
import io.machinecode.chainlink.spi.element.partition.Analyser;
import io.machinecode.chainlink.spi.expression.JobPropertyContext;
import io.machinecode.chainlink.spi.expression.PropertyContext;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class AnalyserFactory implements ElementFactory<Analyser, AnalyserImpl> {

    public static final AnalyserFactory INSTANCE = new AnalyserFactory();

    @Override
    public AnalyserImpl produceExecution(final Analyser that, final JobPropertyContext context) {
        final String ref = Expression.resolveExecutionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceExecution(that.getProperties(), context);
        return new AnalyserImpl(
                context.getReference(new ArtifactReferenceImpl(ref)),
                properties
        );
    }

    @Override
    public AnalyserImpl producePartitioned(final AnalyserImpl that, final PropertyContext context) {
        final String ref = Expression.resolvePartitionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.producePartitioned(that.getProperties(), context);
        return new AnalyserImpl(
                context.getReference(new ArtifactReferenceImpl(ref)),
                properties
        );
    }
}
