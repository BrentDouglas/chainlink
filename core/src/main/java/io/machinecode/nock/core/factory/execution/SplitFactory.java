package io.machinecode.nock.core.factory.execution;

import io.machinecode.nock.core.descriptor.execution.FlowImpl;
import io.machinecode.nock.core.descriptor.execution.SplitImpl;
import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobParameterContext;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ExecutionFactory;
import io.machinecode.nock.core.util.Util;
import io.machinecode.nock.core.util.Util.NextExpressionTransformer;
import io.machinecode.nock.core.work.execution.FlowWork;
import io.machinecode.nock.core.work.execution.SplitWork;
import io.machinecode.nock.spi.element.execution.Execution;
import io.machinecode.nock.spi.element.execution.Flow;
import io.machinecode.nock.spi.element.execution.Split;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class SplitFactory implements ExecutionFactory<Split, SplitImpl, SplitWork> {

    public static final SplitFactory INSTANCE = new SplitFactory();

    private static final NextExpressionTransformer<Flow, FlowImpl, JobPropertyContext> FLOW_BUILD_TRANSFORMER = new NextExpressionTransformer<Flow, FlowImpl, JobPropertyContext>() {
        @Override
        public FlowImpl transform(final Flow that, final Flow next, final JobPropertyContext context) {
            return FlowFactory.INSTANCE.produceDescriptor(that, next, context);
        }
    };
    private static final NextExpressionTransformer<FlowImpl, FlowWork, JobParameterContext> FLOW_EXECUTION_TRANSFORMER = new NextExpressionTransformer<FlowImpl, FlowWork, JobParameterContext>() {
        @Override
        public FlowWork transform(final FlowImpl that, final FlowImpl next, final JobParameterContext context) {
            return FlowFactory.INSTANCE.produceExecution(that, next, context);
        }
    };
    private static final NextExpressionTransformer<FlowWork, FlowWork, PartitionPropertyContext> FLOW_PARTITION_TRANSFORMER = new NextExpressionTransformer<FlowWork, FlowWork, PartitionPropertyContext>() {
        @Override
        public FlowWork transform(final FlowWork that, final FlowWork next, final PartitionPropertyContext context) {
            return FlowFactory.INSTANCE.producePartitioned(that, next, context);
        }
    };

    @Override
    public SplitImpl produceDescriptor(final Split that, final Execution execution, final JobPropertyContext context) {
        final String id = Expression.resolveDescriptorProperty(that.getId(), context);
        final String next = Expression.resolveDescriptorProperty(that.getNext() == null ? execution == null ? null : execution.getId() : that.getNext(), context);
        final List<FlowImpl> flows = Util.immutableCopy(that.getFlows(), context, FLOW_BUILD_TRANSFORMER);
        return new SplitImpl(id, next, flows);
    }

    @Override
    public SplitWork produceExecution(final SplitImpl that, final Execution execution, final JobParameterContext context) {
        final String id = Expression.resolveExecutionProperty(that.getId(), context);
        final String next = Expression.resolveExecutionProperty(that.getNext() == null ? execution == null ? null : execution.getId() : that.getNext(), context);
        final List<FlowWork> flows = Util.immutableCopy(that.getFlows(), context, FLOW_EXECUTION_TRANSFORMER);
        return new SplitWork(id, next, flows);
    }

    @Override
    public SplitWork producePartitioned(final SplitWork that, final Execution execution, final PartitionPropertyContext context) {
        final String id = Expression.resolvePartitionProperty(that.getId(), context);
        final String next = Expression.resolvePartitionProperty(that.getNext() == null ? execution == null ? null : execution.getId() : that.getNext(), context);
        final List<FlowWork> flows = Util.immutableCopy(that.getFlows(), context, FLOW_PARTITION_TRANSFORMER);
        return new SplitWork(id, next, flows);
    }
}
