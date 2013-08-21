package io.machinecode.nock.core.factory;

import io.machinecode.nock.core.descriptor.ListenerImpl;
import io.machinecode.nock.core.descriptor.ListenersImpl;
import io.machinecode.nock.core.expression.JobParameterContext;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.util.Util;
import io.machinecode.nock.core.util.Util.ExpressionTransformer;
import io.machinecode.nock.core.work.ListenerWork;
import io.machinecode.nock.core.work.ListenersWork;
import io.machinecode.nock.spi.element.Listener;
import io.machinecode.nock.spi.element.Listeners;

import javax.batch.api.listener.StepListener;
import java.util.Collections;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class StepListenersFactory implements ElementFactory<Listeners, ListenersImpl, ListenersWork<StepListener>> {

    public static final StepListenersFactory INSTANCE = new StepListenersFactory();

    private static final ExpressionTransformer<Listener, ListenerImpl, JobPropertyContext> LISTENER_BUILD_TRANSFORMER = new ExpressionTransformer<Listener, ListenerImpl, JobPropertyContext>() {
        @Override
        public ListenerImpl transform(final Listener that, final JobPropertyContext context) {
            return StepListenerFactory.INSTANCE.produceDescriptor(that, context);
        }
    };

    private static final ExpressionTransformer<ListenerImpl, ListenerWork<StepListener>, JobParameterContext> STEP_LISTENER_EXECUTION_TRANSFORMER = new ExpressionTransformer<ListenerImpl, ListenerWork<StepListener>, JobParameterContext>() {
        @Override
        public ListenerWork<StepListener> transform(final ListenerImpl that, final JobParameterContext context) {
            return StepListenerFactory.INSTANCE.produceExecution(that, context);
        }
    };

    private static final ExpressionTransformer<ListenerWork<StepListener>, ListenerWork<StepListener>, PartitionPropertyContext> STEP_LISTENER_PARTITION_TRANSFORMER = new ExpressionTransformer<ListenerWork<StepListener>, ListenerWork<StepListener>, PartitionPropertyContext>() {
        @Override
        public ListenerWork<StepListener> transform(final ListenerWork<StepListener> that, final PartitionPropertyContext context) {
            return StepListenerFactory.INSTANCE.producePartitioned(that, context);
        }
    };

    @Override
    public ListenersImpl produceDescriptor(final Listeners that, final JobPropertyContext context) {
        final List<ListenerImpl> listeners = that == null
                ? Collections.<ListenerImpl>emptyList()
                : Util.immutableCopy(that.getListeners(), context, LISTENER_BUILD_TRANSFORMER);
        return new ListenersImpl(
                listeners
        );
    }

    @Override
    public ListenersWork<StepListener> produceExecution(final ListenersImpl that, final JobParameterContext context) {
        return new ListenersWork<StepListener>(
                Util.immutableCopy(that.getListeners(), context, STEP_LISTENER_EXECUTION_TRANSFORMER)
        );
    }

    @Override
    public ListenersWork<StepListener> producePartitioned(final ListenersWork<StepListener> that, final PartitionPropertyContext context) {
        return new ListenersWork<StepListener>(
                Util.immutableCopy(that.getListeners(), context, STEP_LISTENER_PARTITION_TRANSFORMER)
        );
    }
}
