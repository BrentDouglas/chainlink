package io.machinecode.chainlink.core.factory.execution;

import io.machinecode.chainlink.core.expression.Expression;
import io.machinecode.chainlink.core.expression.JobPropertyContext;
import io.machinecode.chainlink.core.expression.PartitionPropertyContext;
import io.machinecode.chainlink.core.jsl.impl.execution.FlowImpl;
import io.machinecode.chainlink.core.jsl.impl.execution.SplitImpl;
import io.machinecode.chainlink.core.util.Copy;
import io.machinecode.chainlink.core.util.Copy.ExpressionTransformer;
import io.machinecode.chainlink.spi.jsl.execution.Flow;
import io.machinecode.chainlink.spi.jsl.execution.Split;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class SplitFactory {

    private static final ExpressionTransformer<Flow, FlowImpl, JobPropertyContext> FLOW_EXECUTION_TRANSFORMER = new ExpressionTransformer<Flow, FlowImpl, JobPropertyContext>() {
        @Override
        public FlowImpl transform(final Flow that, final JobPropertyContext context) {
            return FlowFactory.produceExecution(that, context);
        }
    };
    private static final ExpressionTransformer<FlowImpl, FlowImpl, PartitionPropertyContext> FLOW_PARTITION_TRANSFORMER = new ExpressionTransformer<FlowImpl, FlowImpl, PartitionPropertyContext>() {
        @Override
        public FlowImpl transform(final FlowImpl that, final PartitionPropertyContext context) {
            return FlowFactory.producePartitioned(that, context);
        }
    };

    public static SplitImpl produceExecution(final Split that, final JobPropertyContext context) {
        final String id = Expression.resolveExecutionProperty(that.getId(), context);
        final String next = Expression.resolveExecutionProperty(that.getNext(), context);
        final List<FlowImpl> flows = Copy.immutableCopy(that.getFlows(), context, FLOW_EXECUTION_TRANSFORMER);
        return new SplitImpl(id, next, flows);
    }

    public static SplitImpl producePartitioned(final SplitImpl that, final PartitionPropertyContext context) {
        final String id = Expression.resolvePartitionProperty(that.getId(), context);
        final String next = Expression.resolvePartitionProperty(that.getNext(), context);
        final List<FlowImpl> flows = Copy.immutableCopy(that.getFlows(), context, FLOW_PARTITION_TRANSFORMER);
        return new SplitImpl(id, next, flows);
    }
}
