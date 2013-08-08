package io.machinecode.nock.core.factory.transition;

import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.model.transition.TransitionImpl;
import io.machinecode.nock.core.util.Util;
import io.machinecode.nock.core.util.Util.ExpressionTransformer;
import io.machinecode.nock.core.util.Util.ParametersTransformer;
import io.machinecode.nock.jsl.api.transition.End;
import io.machinecode.nock.jsl.api.transition.Fail;
import io.machinecode.nock.jsl.api.transition.Next;
import io.machinecode.nock.jsl.api.transition.Stop;
import io.machinecode.nock.jsl.api.transition.Transition;

import java.util.List;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class Transitions {

    private static final ExpressionTransformer<Transition, TransitionImpl> TRANSITION_BUILD_TRANSFORMER = new ExpressionTransformer<Transition, TransitionImpl>() {
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

    private static final ParametersTransformer<Transition, TransitionImpl> TRANSITION_START_TRANSFORMER = new ParametersTransformer<Transition, TransitionImpl>() {
        @Override
        public TransitionImpl transform(final Transition that, final Properties parameters) {
            if (that instanceof End) {
                return EndFactory.INSTANCE.produceStartTime((End) that, parameters);
            } else if (that instanceof Fail) {
                return FailFactory.INSTANCE.produceStartTime((Fail) that, parameters);
            } else if (that instanceof Next) {
                return NextFactory.INSTANCE.produceStartTime((Next) that, parameters);
            } else if (that instanceof Stop) {
                return StopFactory.INSTANCE.produceStartTime((Stop) that, parameters);
            }
            return null;
        }
    };

    private static final ExpressionTransformer<Transition, TransitionImpl> TRANSITION_RUN_TRANSFORMER = new ExpressionTransformer<Transition, TransitionImpl>() {
        @Override
        public TransitionImpl transform(final Transition that, final JobPropertyContext context) {
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

    public static List<TransitionImpl> immutableCopyTransitionsStartTime(final List<? extends Transition> that, final Properties parameters) {
        return Util.immutableCopy(that, parameters, TRANSITION_START_TRANSFORMER);
    }

    public static List<TransitionImpl> immutableCopyTransitionsPartitionTime(final List<? extends Transition> that, final JobPropertyContext context) {
        return Util.immutableCopy(that, context, TRANSITION_RUN_TRANSFORMER);
    }
}
