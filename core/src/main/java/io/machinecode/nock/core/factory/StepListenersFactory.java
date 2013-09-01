package io.machinecode.nock.core.factory;

import io.machinecode.nock.core.model.ListenerImpl;
import io.machinecode.nock.core.model.ListenersImpl;
import io.machinecode.nock.core.util.Util;
import io.machinecode.nock.core.util.Util.ExpressionTransformer;
import io.machinecode.nock.spi.factory.ElementFactory;
import io.machinecode.nock.spi.element.Listener;
import io.machinecode.nock.spi.element.Listeners;
import io.machinecode.nock.spi.factory.JobPropertyContext;
import io.machinecode.nock.spi.factory.PropertyContext;

import javax.batch.api.listener.StepListener;
import java.util.Collections;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class StepListenersFactory implements ElementFactory<Listeners, ListenersImpl> {

    public static final StepListenersFactory INSTANCE = new StepListenersFactory();

    private static final ExpressionTransformer<Listener, ListenerImpl, JobPropertyContext> STEP_LISTENER_EXECUTION_TRANSFORMER = new ExpressionTransformer<Listener, ListenerImpl, JobPropertyContext>() {
        @Override
        public ListenerImpl transform(final Listener that, final JobPropertyContext context) {
            return StepListenerFactory.INSTANCE.produceExecution(that, context);
        }
    };

    private static final ExpressionTransformer<ListenerImpl, ListenerImpl, PropertyContext> STEP_LISTENER_PARTITION_TRANSFORMER = new ExpressionTransformer<ListenerImpl, ListenerImpl, PropertyContext>() {
        @Override
        public ListenerImpl transform(final ListenerImpl that, final PropertyContext context) {
            return StepListenerFactory.INSTANCE.producePartitioned(that, context);
        }
    };

    @Override
    public ListenersImpl produceExecution(final Listeners that, final JobPropertyContext context) {
        final List<ListenerImpl> listeners = that == null
                ? Collections.<ListenerImpl>emptyList()
                : Util.immutableCopy(that.getListeners(), context, STEP_LISTENER_EXECUTION_TRANSFORMER);
        return new ListenersImpl(
                listeners
        );
    }

    @Override
    public ListenersImpl producePartitioned(final ListenersImpl that, final PropertyContext context) {
        return new ListenersImpl(
                Util.immutableCopy(that.getListeners(), context, STEP_LISTENER_PARTITION_TRANSFORMER)
        );
    }
}
