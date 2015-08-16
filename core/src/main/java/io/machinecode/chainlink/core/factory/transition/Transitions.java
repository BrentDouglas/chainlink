/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
package io.machinecode.chainlink.core.factory.transition;

import io.machinecode.chainlink.core.expression.JobPropertyContext;
import io.machinecode.chainlink.core.expression.PartitionPropertyContext;
import io.machinecode.chainlink.core.jsl.impl.transition.EndImpl;
import io.machinecode.chainlink.core.jsl.impl.transition.FailImpl;
import io.machinecode.chainlink.core.jsl.impl.transition.NextImpl;
import io.machinecode.chainlink.core.jsl.impl.transition.StopImpl;
import io.machinecode.chainlink.core.jsl.impl.transition.TransitionImpl;
import io.machinecode.chainlink.core.util.Copy;
import io.machinecode.chainlink.core.util.Copy.ExpressionTransformer;
import io.machinecode.chainlink.spi.jsl.transition.End;
import io.machinecode.chainlink.spi.jsl.transition.Fail;
import io.machinecode.chainlink.spi.jsl.transition.Next;
import io.machinecode.chainlink.spi.jsl.transition.Stop;
import io.machinecode.chainlink.spi.jsl.transition.Transition;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class Transitions {

    private static final ExpressionTransformer<Transition, TransitionImpl, JobPropertyContext> TRANSITION_EXECUTION_TRANSFORMER = new ExpressionTransformer<Transition, TransitionImpl, JobPropertyContext>() {
        @Override
        public TransitionImpl transform(final Transition that, final JobPropertyContext context) {
            if (that instanceof End) {
                return EndFactory.produceExecution((End) that, context);
            } else if (that instanceof Fail) {
                return FailFactory.produceExecution((Fail) that, context);
            } else if (that instanceof Next) {
                return NextFactory.produceExecution((Next) that, context);
            } else if (that instanceof Stop) {
                return StopFactory.produceExecution((Stop) that, context);
            }
            return null;
        }
    };

    private static final ExpressionTransformer<TransitionImpl, TransitionImpl, PartitionPropertyContext> TRANSITION_PARTITION_TRANSFORMER = new ExpressionTransformer<TransitionImpl, TransitionImpl, PartitionPropertyContext>() {
        @Override
        public TransitionImpl transform(final TransitionImpl that, final PartitionPropertyContext context) {
            if (that instanceof EndImpl) {
                return EndFactory.producePartitioned((EndImpl) that, context);
            } else if (that instanceof FailImpl) {
                return FailFactory.producePartitioned((FailImpl) that, context);
            } else if (that instanceof NextImpl) {
                return NextFactory.producePartitioned((NextImpl) that, context);
            } else if (that instanceof StopImpl) {
                return StopFactory.producePartitioned((StopImpl) that, context);
            }
            return null;
        }
    };


    public static List<TransitionImpl> immutableCopyTransitionsDescriptor(final List<? extends Transition> that, final JobPropertyContext context) {
        return Copy.immutableCopy(that, context, TRANSITION_EXECUTION_TRANSFORMER);
    }

    public static List<TransitionImpl> immutableCopyTransitionsPartition(final List<TransitionImpl> that, final PartitionPropertyContext context) {
        return Copy.immutableCopy(that, context, TRANSITION_PARTITION_TRANSFORMER);
    }
}
