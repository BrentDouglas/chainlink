package io.machinecode.chainlink.core.factory.transition;

import io.machinecode.chainlink.core.element.transition.EndImpl;
import io.machinecode.chainlink.core.element.transition.FailImpl;
import io.machinecode.chainlink.core.element.transition.NextImpl;
import io.machinecode.chainlink.core.element.transition.StopImpl;
import io.machinecode.chainlink.core.element.transition.TransitionImpl;
import io.machinecode.chainlink.core.util.Util;
import io.machinecode.chainlink.core.util.Util.ExpressionTransformer;
import io.machinecode.chainlink.spi.element.transition.End;
import io.machinecode.chainlink.spi.element.transition.Fail;
import io.machinecode.chainlink.spi.element.transition.Next;
import io.machinecode.chainlink.spi.element.transition.Stop;
import io.machinecode.chainlink.spi.element.transition.Transition;
import io.machinecode.chainlink.spi.expression.JobPropertyContext;
import io.machinecode.chainlink.spi.expression.PropertyContext;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class Transitions {

    private static final ExpressionTransformer<Transition, TransitionImpl, JobPropertyContext> TRANSITION_EXECUTION_TRANSFORMER = new ExpressionTransformer<Transition, TransitionImpl, JobPropertyContext>() {
        @Override
        public TransitionImpl transform(final Transition that, final JobPropertyContext context) {
            if (that instanceof End) {
                return EndFactory.INSTANCE.produceExecution((End) that, context);
            } else if (that instanceof Fail) {
                return FailFactory.INSTANCE.produceExecution((Fail) that, context);
            } else if (that instanceof Next) {
                return NextFactory.INSTANCE.produceExecution((Next) that, context);
            } else if (that instanceof Stop) {
                return StopFactory.INSTANCE.produceExecution((Stop) that, context);
            }
            return null;
        }
    };

    private static final ExpressionTransformer<TransitionImpl, TransitionImpl, PropertyContext> TRANSITION_PARTITION_TRANSFORMER = new ExpressionTransformer<TransitionImpl, TransitionImpl, PropertyContext>() {
        @Override
        public TransitionImpl transform(final TransitionImpl that, final PropertyContext context) {
            if (that instanceof EndImpl) {
                return EndFactory.INSTANCE.producePartitioned((EndImpl) that, context);
            } else if (that instanceof FailImpl) {
                return FailFactory.INSTANCE.producePartitioned((FailImpl) that, context);
            } else if (that instanceof NextImpl) {
                return NextFactory.INSTANCE.producePartitioned((NextImpl) that, context);
            } else if (that instanceof StopImpl) {
                return StopFactory.INSTANCE.producePartitioned((StopImpl) that, context);
            }
            return null;
        }
    };


    public static List<TransitionImpl> immutableCopyTransitionsDescriptor(final List<? extends Transition> that, final JobPropertyContext context) {
        return Util.immutableCopy(that, context, TRANSITION_EXECUTION_TRANSFORMER);
    }

    public static List<TransitionImpl> immutableCopyTransitionsPartition(final List<TransitionImpl> that, final PropertyContext context) {
        return Util.immutableCopy(that, context, TRANSITION_PARTITION_TRANSFORMER);
    }
}
