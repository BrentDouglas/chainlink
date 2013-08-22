package io.machinecode.nock.core.factory;

import io.machinecode.nock.core.model.ListenerImpl;
import io.machinecode.nock.core.model.ListenersImpl;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.util.Util;
import io.machinecode.nock.core.util.Util.ExpressionTransformer;
import io.machinecode.nock.spi.element.Listener;
import io.machinecode.nock.spi.element.Listeners;

import javax.batch.api.listener.StepListener;
import java.util.Collections;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class StepListenersFactory implements ElementFactory<Listeners, ListenersImpl<StepListener>> {

    public static final StepListenersFactory INSTANCE = new StepListenersFactory();

    private static final ExpressionTransformer<Listener, ListenerImpl<StepListener>, JobPropertyContext> STEP_LISTENER_EXECUTION_TRANSFORMER = new ExpressionTransformer<Listener, ListenerImpl<StepListener>, JobPropertyContext>() {
        @Override
        public ListenerImpl<StepListener> transform(final Listener that, final JobPropertyContext context) {
            return StepListenerFactory.INSTANCE.produceExecution(that, context);
        }
    };

    private static final ExpressionTransformer<ListenerImpl<StepListener>, ListenerImpl<StepListener>, PartitionPropertyContext> STEP_LISTENER_PARTITION_TRANSFORMER = new ExpressionTransformer<ListenerImpl<StepListener>, ListenerImpl<StepListener>, PartitionPropertyContext>() {
        @Override
        public ListenerImpl<StepListener> transform(final ListenerImpl<StepListener> that, final PartitionPropertyContext context) {
            return StepListenerFactory.INSTANCE.producePartitioned(that, context);
        }
    };

    @Override
    public ListenersImpl<StepListener> produceExecution(final Listeners that, final JobPropertyContext context) {
        final List<ListenerImpl<StepListener>> listeners = that == null
                ? Collections.<ListenerImpl<StepListener>>emptyList()
                : Util.immutableCopy(that.getListeners(), context, STEP_LISTENER_EXECUTION_TRANSFORMER);
        return new ListenersImpl<StepListener>(
                listeners
        );
    }

    @Override
    public ListenersImpl<StepListener> producePartitioned(final ListenersImpl<StepListener> that, final PartitionPropertyContext context) {
        return new ListenersImpl<StepListener>(
                Util.immutableCopy(that.getListeners(), context, STEP_LISTENER_PARTITION_TRANSFORMER)
        );
    }
}
