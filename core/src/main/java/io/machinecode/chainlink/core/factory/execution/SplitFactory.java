package io.machinecode.chainlink.core.factory.execution;

import io.machinecode.chainlink.core.element.execution.FlowImpl;
import io.machinecode.chainlink.core.element.execution.SplitImpl;
import io.machinecode.chainlink.core.expression.Expression;
import io.machinecode.chainlink.core.factory.ElementFactory;
import io.machinecode.chainlink.core.util.Util;
import io.machinecode.chainlink.core.util.Util.ExpressionTransformer;
import io.machinecode.chainlink.spi.element.execution.Flow;
import io.machinecode.chainlink.spi.element.execution.Split;
import io.machinecode.chainlink.spi.expression.JobPropertyContext;
import io.machinecode.chainlink.spi.expression.PropertyContext;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class SplitFactory implements ElementFactory<Split, SplitImpl> {

    public static final SplitFactory INSTANCE = new SplitFactory();

    private static final ExpressionTransformer<Flow, FlowImpl, JobPropertyContext> FLOW_EXECUTION_TRANSFORMER = new ExpressionTransformer<Flow, FlowImpl, JobPropertyContext>() {
        @Override
        public FlowImpl transform(final Flow that, final JobPropertyContext context) {
            return FlowFactory.INSTANCE.produceExecution(that, context);
        }
    };
    private static final ExpressionTransformer<FlowImpl, FlowImpl, PropertyContext> FLOW_PARTITION_TRANSFORMER = new ExpressionTransformer<FlowImpl, FlowImpl, PropertyContext>() {
        @Override
        public FlowImpl transform(final FlowImpl that, final PropertyContext context) {
            return FlowFactory.INSTANCE.producePartitioned(that, context);
        }
    };

    @Override
    public SplitImpl produceExecution(final Split that, final JobPropertyContext context) {
        final String id = Expression.resolveExecutionProperty(that.getId(), context);
        final String next = Expression.resolveExecutionProperty(that.getNext(), context);
        final List<FlowImpl> flows = Util.immutableCopy(that.getFlows(), context, FLOW_EXECUTION_TRANSFORMER);
        return new SplitImpl(id, next, flows);
    }

    @Override
    public SplitImpl producePartitioned(final SplitImpl that, final PropertyContext context) {
        final String id = Expression.resolvePartitionProperty(that.getId(), context);
        final String next = Expression.resolvePartitionProperty(that.getNext(), context);
        final List<FlowImpl> flows = Util.immutableCopy(that.getFlows(), context, FLOW_PARTITION_TRANSFORMER);
        return new SplitImpl(id, next, flows);
    }
}
