package io.machinecode.nock.core.factory;

import io.machinecode.nock.core.model.ListenerImpl;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.inject.ResolvableReference;
import io.machinecode.nock.spi.element.Listener;

import javax.batch.api.listener.StepListener;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class StepListenerFactory implements ElementFactory<Listener, ListenerImpl<StepListener>> {

    public static final StepListenerFactory INSTANCE = new StepListenerFactory();

    @Override
    public ListenerImpl<StepListener> produceExecution(final Listener that, final JobPropertyContext context) {
        final String ref = Expression.resolveExecutionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceExecution(that.getProperties(), context);
        return new ListenerImpl<StepListener>(
                new ResolvableReference<StepListener>(ref, StepListener.class),
                properties
        );
    }

    @Override
    public ListenerImpl<StepListener> producePartitioned(final ListenerImpl<StepListener> that, final PartitionPropertyContext context) {
        final String ref = Expression.resolvePartitionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.producePartitioned(that.getProperties(), context);
        return new ListenerImpl<StepListener>(
                new ResolvableReference<StepListener>(ref, StepListener.class),
                properties
        );
    }
}
