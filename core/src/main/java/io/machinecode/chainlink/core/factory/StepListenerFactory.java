package io.machinecode.chainlink.core.factory;

import io.machinecode.chainlink.core.expression.Expression;
import io.machinecode.chainlink.core.expression.JobPropertyContext;
import io.machinecode.chainlink.core.inject.ArtifactReferenceImpl;
import io.machinecode.chainlink.core.jsl.impl.ListenerImpl;
import io.machinecode.chainlink.core.jsl.impl.PropertiesImpl;
import io.machinecode.chainlink.spi.expression.PropertyContext;
import io.machinecode.chainlink.spi.jsl.Listener;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class StepListenerFactory implements ElementFactory<Listener, ListenerImpl> {

    public static final StepListenerFactory INSTANCE = new StepListenerFactory();

    @Override
    public ListenerImpl produceExecution(final Listener that, final JobPropertyContext context) {
        final String ref = Expression.resolveExecutionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceExecution(that.getProperties(), context);
        return new ListenerImpl(
                context.getReference(new ArtifactReferenceImpl(ref)),
                properties
        );
    }

    @Override
    public ListenerImpl producePartitioned(final ListenerImpl that, final PropertyContext context) {
        final String ref = Expression.resolvePartitionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.producePartitioned(that.getProperties(), context);
        return new ListenerImpl(
                context.getReference(new ArtifactReferenceImpl(ref)),
                properties
        );
    }
}
