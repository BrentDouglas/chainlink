/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @author tags. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.machinecode.chainlink.core.factory;

import io.machinecode.chainlink.core.expression.JobPropertyContext;
import io.machinecode.chainlink.core.expression.PartitionPropertyContext;
import io.machinecode.chainlink.core.jsl.impl.ListenerImpl;
import io.machinecode.chainlink.core.jsl.impl.ListenersImpl;
import io.machinecode.chainlink.core.util.Copy;
import io.machinecode.chainlink.core.util.Copy.ExpressionTransformer;
import io.machinecode.chainlink.spi.jsl.Listener;
import io.machinecode.chainlink.spi.jsl.Listeners;

import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class StepListenersFactory {

    private static final ExpressionTransformer<Listener, ListenerImpl, JobPropertyContext> STEP_LISTENER_EXECUTION_TRANSFORMER = new ExpressionTransformer<Listener, ListenerImpl, JobPropertyContext>() {
        @Override
        public ListenerImpl transform(final Listener that, final JobPropertyContext context) {
            return StepListenerFactory.produceExecution(that, context);
        }
    };

    private static final ExpressionTransformer<ListenerImpl, ListenerImpl, PartitionPropertyContext> STEP_LISTENER_PARTITION_TRANSFORMER = new ExpressionTransformer<ListenerImpl, ListenerImpl, PartitionPropertyContext>() {
        @Override
        public ListenerImpl transform(final ListenerImpl that, final PartitionPropertyContext context) {
            return StepListenerFactory.producePartitioned(that, context);
        }
    };

    public static ListenersImpl produceExecution(final Listeners that, final JobPropertyContext context) {
        final List<ListenerImpl> listeners = that == null
                ? Collections.<ListenerImpl>emptyList()
                : Copy.immutableCopy(that.getListeners(), context, STEP_LISTENER_EXECUTION_TRANSFORMER);
        return new ListenersImpl(
                listeners
        );
    }

    public static ListenersImpl producePartitioned(final ListenersImpl that, final PartitionPropertyContext context) {
        return new ListenersImpl(
                Copy.immutableCopy(that.getListeners(), context, STEP_LISTENER_PARTITION_TRANSFORMER)
        );
    }
}
