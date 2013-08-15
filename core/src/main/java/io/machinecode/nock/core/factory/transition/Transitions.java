package io.machinecode.nock.core.factory.transition;

import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.model.transition.TransitionImpl;
import io.machinecode.nock.core.util.Util;
import io.machinecode.nock.core.util.Util.ExpressionTransformer;
import io.machinecode.nock.jsl.api.transition.End;
import io.machinecode.nock.jsl.api.transition.Fail;
import io.machinecode.nock.jsl.api.transition.Next;
import io.machinecode.nock.jsl.api.transition.Stop;
import io.machinecode.nock.jsl.api.transition.Transition;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class Transitions {

    private static final ExpressionTransformer<Transition, TransitionImpl, JobPropertyContext> TRANSITION_BUILD_TRANSFORMER = new ExpressionTransformer<Transition, TransitionImpl, JobPropertyContext>() {
        @Override
        public TransitionImpl transform(final Transition that, final JobPropertyContext context) {
            if (that instanceof End) {
                return EndFactory.INSTANCE.produceBuildTime((End) that, context);
            } else if (that instanceof Fail) {
                return FailFactory.INSTANCE.produceBuildTime((Fail) that, context);
            } else if (that instanceof Next) {
                return NextFactory.INSTANCE.produceBuildTime((Next) that, context);
            } else if (that instanceof Stop) {
                return StopFactory.INSTANCE.produceBuildTime((Stop) that, context);
            }
            return null;
        }
    };

    private static final ExpressionTransformer<Transition, TransitionImpl, PartitionPropertyContext> TRANSITION_RUN_TRANSFORMER = new ExpressionTransformer<Transition, TransitionImpl, PartitionPropertyContext>() {
        @Override
        public TransitionImpl transform(final Transition that, final PartitionPropertyContext context) {
            if (that instanceof End) {
                return EndFactory.INSTANCE.producePartitionTime((End) that, context);
            } else if (that instanceof Fail) {
                return FailFactory.INSTANCE.producePartitionTime((Fail) that, context);
            } else if (that instanceof Next) {
                return NextFactory.INSTANCE.producePartitionTime((Next) that, context);
            } else if (that instanceof Stop) {
                return StopFactory.INSTANCE.producePartitionTime((Stop) that, context);
            }
            return null;
        }
    };


    public static List<TransitionImpl> immutableCopyTransitionsBuildTime(final List<? extends Transition> that, final JobPropertyContext context) {
        return Util.immutableCopy(that, context, TRANSITION_BUILD_TRANSFORMER);
    }

    public static List<TransitionImpl> immutableCopyTransitionsPartitionTime(final List<? extends Transition> that, final PartitionPropertyContext context) {
        return Util.immutableCopy(that, context, TRANSITION_RUN_TRANSFORMER);
    }
}
