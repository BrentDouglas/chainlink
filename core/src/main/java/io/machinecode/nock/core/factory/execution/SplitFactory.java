package io.machinecode.nock.core.factory.execution;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.factory.ExecutionFactory;
import io.machinecode.nock.core.model.execution.FlowImpl;
import io.machinecode.nock.core.model.execution.SplitImpl;
import io.machinecode.nock.core.util.Util;
import io.machinecode.nock.core.util.Util.ExpressionTransformer;
import io.machinecode.nock.core.util.Util.NextExpressionTransformer;
import io.machinecode.nock.core.util.Util.ParametersTransformer;
import io.machinecode.nock.jsl.api.execution.Execution;
import io.machinecode.nock.jsl.api.execution.Flow;
import io.machinecode.nock.jsl.api.execution.Split;
import io.machinecode.nock.jsl.util.MutablePair;

import java.util.List;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class SplitFactory implements ExecutionFactory<Split, SplitImpl> {

    public static final SplitFactory INSTANCE = new SplitFactory();

    private static final NextExpressionTransformer<Flow, FlowImpl> FLOW_BUILD_TRANSFORMER = new NextExpressionTransformer<Flow, FlowImpl>() {
        @Override
        public FlowImpl transform(final Flow that, final Flow next, final JobPropertyContext context) {
            return FlowFactory.INSTANCE.produceBuildTime(that, next, context);
        }
    };
    private static final ParametersTransformer<Flow, FlowImpl> FLOW_START_TRANSFORMER = new ParametersTransformer<Flow, FlowImpl>() {
        @Override
        public FlowImpl transform(final Flow that, final Properties parameters) {
            return FlowFactory.INSTANCE.produceStartTime(that, parameters);
        }
    };
    private static final ExpressionTransformer<Flow, FlowImpl> FLOW_PARTITION_TRANSFORMER = new ExpressionTransformer<Flow, FlowImpl>() {
        @Override
        public FlowImpl transform(final Flow that, final JobPropertyContext context) {
            return FlowFactory.INSTANCE.producePartitionTime(that, context);
        }
    };

    @Override
    public SplitImpl produceBuildTime(final Split that, final Execution execution, final JobPropertyContext context) {
        final List<MutablePair<String,String>> jobProperties = context.getProperties();
        final String id = Expression.resolveBuildTime(that.getId(), jobProperties);
        final String next = Expression.resolveBuildTime(that.getNext() == null ? execution == null ? null : execution.getId() : that.getNext(), jobProperties);
        final List<FlowImpl> flows = Util.immutableCopy(that.getFlows(), context, FLOW_BUILD_TRANSFORMER);
        return new SplitImpl(id, next, flows);
    }

    @Override
    public SplitImpl produceStartTime(final Split that, final Properties parameters) {
        final String id = Expression.resolveStartTime(that.getId(), parameters);
        final String next = Expression.resolveStartTime(that.getNext(), parameters);
        final List<FlowImpl> flows = Util.immutableCopy(that.getFlows(), parameters, FLOW_START_TRANSFORMER);
        return new SplitImpl(id, next, flows);
    }

    @Override
    public SplitImpl producePartitionTime(final Split that, final JobPropertyContext context) {
        final List<MutablePair<String,String>> partitionPlan = context.getProperties();
        final String id = Expression.resolvePartition(that.getId(), partitionPlan);
        final String next = Expression.resolvePartition(that.getNext(), partitionPlan);
        final List<FlowImpl> flows = Util.immutableCopy(that.getFlows(), context, FLOW_PARTITION_TRANSFORMER);
        return new SplitImpl(id, next, flows);
    }
}
