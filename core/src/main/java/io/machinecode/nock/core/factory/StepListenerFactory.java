package io.machinecode.nock.core.factory;

import io.machinecode.nock.core.descriptor.ListenerImpl;
import io.machinecode.nock.core.descriptor.PropertiesImpl;
import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobParameterContext;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.inject.ResolvableReference;
import io.machinecode.nock.core.work.ListenerWork;
import io.machinecode.nock.spi.element.Listener;

import javax.batch.api.listener.StepListener;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class StepListenerFactory implements ElementFactory<Listener, ListenerImpl, ListenerWork<StepListener>> {

    public static final StepListenerFactory INSTANCE = new StepListenerFactory();

    @Override
    public ListenerImpl produceDescriptor(final Listener that, final JobPropertyContext context) {
        final String ref = Expression.resolveDescriptorProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceDescriptor(that.getProperties(), context);
        return new ListenerImpl(
            ref,
            properties
        );
    }

    @Override
    public ListenerWork<StepListener> produceExecution(final ListenerImpl that, final JobParameterContext context) {
        final String ref = Expression.resolveExecutionProperty(that.getRef(), context);
        return new ListenerWork<StepListener>(new ResolvableReference<StepListener>(
                ref,
                StepListener.class
        ));
    }

    @Override
    public ListenerWork<StepListener> producePartitioned(final ListenerWork<StepListener> that, final PartitionPropertyContext context) {
        final String ref = Expression.resolvePartitionProperty(that.getRef(), context);
        return new ListenerWork<StepListener>(new ResolvableReference<StepListener>(
                ref,
                StepListener.class
        ));
    }
}
