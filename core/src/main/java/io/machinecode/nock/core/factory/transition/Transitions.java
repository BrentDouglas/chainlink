package io.machinecode.nock.core.factory.transition;

import io.machinecode.nock.core.descriptor.transition.EndImpl;
import io.machinecode.nock.core.descriptor.transition.FailImpl;
import io.machinecode.nock.core.descriptor.transition.NextImpl;
import io.machinecode.nock.core.descriptor.transition.StopImpl;
import io.machinecode.nock.core.descriptor.transition.TransitionImpl;
import io.machinecode.nock.core.expression.JobParameterContext;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.util.Util;
import io.machinecode.nock.core.util.Util.ExpressionTransformer;
import io.machinecode.nock.core.work.transition.EndWork;
import io.machinecode.nock.core.work.transition.FailWork;
import io.machinecode.nock.core.work.transition.NextWork;
import io.machinecode.nock.core.work.transition.StopWork;
import io.machinecode.nock.core.work.transition.TransitionWork;
import io.machinecode.nock.spi.element.transition.End;
import io.machinecode.nock.spi.element.transition.Fail;
import io.machinecode.nock.spi.element.transition.Next;
import io.machinecode.nock.spi.element.transition.Stop;
import io.machinecode.nock.spi.element.transition.Transition;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class Transitions {

    private static final ExpressionTransformer<Transition, TransitionImpl, JobPropertyContext> TRANSITION_BUILD_TRANSFORMER = new ExpressionTransformer<Transition, TransitionImpl, JobPropertyContext>() {
        @Override
        public TransitionImpl transform(final Transition that, final JobPropertyContext context) {
            if (that instanceof End) {
                return EndFactory.INSTANCE.produceDescriptor((End) that, context);
            } else if (that instanceof Fail) {
                return FailFactory.INSTANCE.produceDescriptor((Fail) that, context);
            } else if (that instanceof Next) {
                return NextFactory.INSTANCE.produceDescriptor((Next) that, context);
            } else if (that instanceof Stop) {
                return StopFactory.INSTANCE.produceDescriptor((Stop) that, context);
            }
            return null;
        }
    };

    private static final ExpressionTransformer<TransitionImpl, TransitionWork, JobParameterContext> TRANSITION_EXECUTION_TRANSFORMER = new ExpressionTransformer<TransitionImpl, TransitionWork, JobParameterContext>() {
        @Override
        public TransitionWork transform(final TransitionImpl that, final JobParameterContext context) {
            if (that instanceof EndImpl) {
                return EndFactory.INSTANCE.produceExecution((EndImpl) that, context);
            } else if (that instanceof FailImpl) {
                return FailFactory.INSTANCE.produceExecution((FailImpl) that, context);
            } else if (that instanceof NextImpl) {
                return NextFactory.INSTANCE.produceExecution((NextImpl) that, context);
            } else if (that instanceof StopImpl) {
                return StopFactory.INSTANCE.produceExecution((StopImpl) that, context);
            }
            return null;
        }
    };

    private static final ExpressionTransformer<TransitionWork, TransitionWork, PartitionPropertyContext> TRANSITION_PARTITION_TRANSFORMER = new ExpressionTransformer<TransitionWork, TransitionWork, PartitionPropertyContext>() {
        @Override
        public TransitionWork transform(final TransitionWork that, final PartitionPropertyContext context) {
            if (that instanceof EndWork) {
                return EndFactory.INSTANCE.producePartitioned((EndWork) that, context);
            } else if (that instanceof FailWork) {
                return FailFactory.INSTANCE.producePartitioned((FailWork) that, context);
            } else if (that instanceof NextWork) {
                return NextFactory.INSTANCE.producePartitioned((NextWork) that, context);
            } else if (that instanceof StopWork) {
                return StopFactory.INSTANCE.producePartitioned((StopWork) that, context);
            }
            return null;
        }
    };


    public static List<TransitionImpl> immutableCopyTransitionsDescriptor(final List<? extends Transition> that, final JobPropertyContext context) {
        return Util.immutableCopy(that, context, TRANSITION_BUILD_TRANSFORMER);
    }

    public static List<TransitionWork> immutableCopyTransitionsExecution(final List<? extends TransitionImpl> that, final JobParameterContext context) {
        return Util.immutableCopy(that, context, TRANSITION_EXECUTION_TRANSFORMER);
    }

    public static List<TransitionWork> immutableCopyTransitionsPartition(final List<TransitionWork> that, final PartitionPropertyContext context) {
        return Util.immutableCopy(that, context, TRANSITION_PARTITION_TRANSFORMER);
    }
}
