package io.machinecode.nock.core.factory.execution;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ExecutionFactory;
import io.machinecode.nock.core.model.execution.FlowImpl;
import io.machinecode.nock.core.model.execution.SplitImpl;
import io.machinecode.nock.core.util.Util;
import io.machinecode.nock.core.util.Util.ExpressionTransformer;
import io.machinecode.nock.core.util.Util.NextExpressionTransformer;
import io.machinecode.nock.jsl.api.execution.Execution;
import io.machinecode.nock.jsl.api.execution.Flow;
import io.machinecode.nock.jsl.api.execution.Split;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class SplitFactory implements ExecutionFactory<Split, SplitImpl> {

    public static final SplitFactory INSTANCE = new SplitFactory();

    private static final NextExpressionTransformer<Flow, FlowImpl, JobPropertyContext> FLOW_BUILD_TRANSFORMER = new NextExpressionTransformer<Flow, FlowImpl, JobPropertyContext>() {
        @Override
        public FlowImpl transform(final Flow that, final Flow next, final JobPropertyContext context) {
            return FlowFactory.INSTANCE.produceBuildTime(that, next, context);
        }
    };
    private static final ExpressionTransformer<Flow, FlowImpl, PartitionPropertyContext> FLOW_PARTITION_TRANSFORMER = new ExpressionTransformer<Flow, FlowImpl, PartitionPropertyContext>() {
        @Override
        public FlowImpl transform(final Flow that, final PartitionPropertyContext context) {
            return FlowFactory.INSTANCE.producePartitionTime(that, context);
        }
    };

    @Override
    public SplitImpl produceBuildTime(final Split that, final Execution execution, final JobPropertyContext context) {
        final String id = Expression.resolveBuildTime(that.getId(), context);
        final String next = Expression.resolveBuildTime(that.getNext() == null ? execution == null ? null : execution.getId() : that.getNext(), context);
        final List<FlowImpl> flows = Util.immutableCopy(that.getFlows(), context, FLOW_BUILD_TRANSFORMER);
        return new SplitImpl(id, next, flows);
    }

    @Override
    public SplitImpl producePartitionTime(final Split that, final PartitionPropertyContext context) {
        final String id = Expression.resolvePartition(that.getId(), context);
        final String next = Expression.resolvePartition(that.getNext(), context);
        final List<FlowImpl> flows = Util.immutableCopy(that.getFlows(), context, FLOW_PARTITION_TRANSFORMER);
        return new SplitImpl(id, next, flows);
    }
}
