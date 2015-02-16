package io.machinecode.chainlink.core.factory.transition;

import io.machinecode.chainlink.core.expression.JobPropertyContext;
import io.machinecode.chainlink.core.expression.PropertyContext;
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
        return Copy.immutableCopy(that, context, TRANSITION_EXECUTION_TRANSFORMER);
    }

    public static List<TransitionImpl> immutableCopyTransitionsPartition(final List<TransitionImpl> that, final PropertyContext context) {
        return Copy.immutableCopy(that, context, TRANSITION_PARTITION_TRANSFORMER);
    }
}
