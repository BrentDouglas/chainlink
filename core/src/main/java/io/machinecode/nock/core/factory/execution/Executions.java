package io.machinecode.nock.core.factory.execution;

import io.machinecode.nock.core.model.execution.DecisionImpl;
import io.machinecode.nock.core.model.execution.ExecutionImpl;
import io.machinecode.nock.core.model.execution.FlowImpl;
import io.machinecode.nock.core.model.execution.SplitImpl;
import io.machinecode.nock.core.model.execution.StepImpl;
import io.machinecode.nock.core.model.partition.MapperImpl;
import io.machinecode.nock.core.model.partition.PlanImpl;
import io.machinecode.nock.core.model.task.BatchletImpl;
import io.machinecode.nock.core.model.task.ChunkImpl;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.util.Util;
import io.machinecode.nock.core.util.Util.NextExpressionTransformer;
import io.machinecode.nock.spi.element.execution.Decision;
import io.machinecode.nock.spi.element.execution.Execution;
import io.machinecode.nock.spi.element.execution.Flow;
import io.machinecode.nock.spi.element.execution.Split;
import io.machinecode.nock.spi.element.execution.Step;
import io.machinecode.nock.spi.element.partition.Mapper;
import io.machinecode.nock.spi.element.partition.Partition;
import io.machinecode.nock.spi.element.partition.Plan;
import io.machinecode.nock.spi.element.partition.Strategy;
import io.machinecode.nock.spi.element.task.Batchlet;
import io.machinecode.nock.spi.element.task.Chunk;
import io.machinecode.nock.spi.element.task.Task;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class Executions {

    private static final NextExpressionTransformer<Execution, ExecutionImpl, JobPropertyContext> EXECUTION_BUILD_TRANSFORMER = new NextExpressionTransformer<Execution, ExecutionImpl, JobPropertyContext>() {
        @Override
        public ExecutionImpl transform(final Execution that, final Execution next, final JobPropertyContext context) {
            if (that instanceof Flow) {
                return FlowFactory.INSTANCE.produceExecution((Flow) that, next, context);
            } else if (that instanceof Split) {
                return SplitFactory.INSTANCE.produceExecution((Split) that, next, context);
            } else if (that instanceof Step) {
                final Task task = ((Step) that).getTask();
                final Partition partition = ((Step) that).getPartition();
                final Strategy strategy = partition == null ? null : partition.getStrategy();
                //There is no real reason for these selections on null values
                if (task == null || task instanceof Batchlet) {
                    if (strategy == null || strategy instanceof Mapper) {
                        return BatchletMapperStepFactory.INSTANCE.produceExecution((Step<Batchlet, Mapper>) that, next, context);
                    } else if (strategy instanceof Plan) {
                        return BatchletPlanStepFactory.INSTANCE.produceExecution((Step<Batchlet, Plan>) that, next, context);
                    }
                } else if (task instanceof Chunk) {
                    if (strategy == null || strategy instanceof Mapper) {
                        return ChunkMapperStepFactory.INSTANCE.produceExecution((Step<Chunk, Mapper>) that, next, context);
                    } else if (strategy instanceof Plan) {
                        return ChunkPlanStepFactory.INSTANCE.produceExecution((Step<Chunk, Plan>) that, next, context);
                    }
                }
            } else if (that instanceof Decision) {
                return DecisionFactory.INSTANCE.produceExecution((Decision) that, context);
            }
            return null;
        }
    };

    private static final NextExpressionTransformer<ExecutionImpl, ExecutionImpl, PartitionPropertyContext> EXECUTION_PARTITION_TRANSFORMER = new NextExpressionTransformer<ExecutionImpl, ExecutionImpl, PartitionPropertyContext>() {
        @Override
        public ExecutionImpl transform(final ExecutionImpl that, final ExecutionImpl next, final PartitionPropertyContext context) {
            if (that instanceof FlowImpl) {
                return FlowFactory.INSTANCE.producePartitioned((FlowImpl) that, next, context);
            } else if (that instanceof SplitImpl) {
                return SplitFactory.INSTANCE.producePartitioned((SplitImpl) that, next, context);
            } else if (that instanceof StepImpl) {
                final Task task = ((StepImpl) that).getTask();
                final Partition partition = ((StepImpl) that).getPartition();
                final Strategy strategy = partition == null ? null : partition.getStrategy();
                //There is no real reason for these selections on null values
                if (task == null || task instanceof Batchlet) {
                    if (strategy == null || strategy instanceof Mapper) {
                        return BatchletMapperStepFactory.INSTANCE.producePartitioned((StepImpl<BatchletImpl, MapperImpl>) that, next, context);
                    } else if (strategy instanceof Plan) {
                        return BatchletPlanStepFactory.INSTANCE.producePartitioned((StepImpl<BatchletImpl, PlanImpl>) that, next, context);
                    }
                } else if (task instanceof Chunk) {
                    if (strategy == null || strategy instanceof Mapper) {
                        return ChunkMapperStepFactory.INSTANCE.producePartitioned((StepImpl<ChunkImpl, MapperImpl>) that, next, context);
                    } else if (strategy instanceof Plan) {
                        return ChunkPlanStepFactory.INSTANCE.producePartitioned((StepImpl<ChunkImpl, PlanImpl>) that, next, context);
                    }
                }
            } else if (that instanceof DecisionImpl) {
                return DecisionFactory.INSTANCE.producePartitioned((DecisionImpl) that, context);
            }
            return null;
        }
    };


    public static List<ExecutionImpl> immutableCopyExecutionsDescriptor(final List<? extends Execution> that, final JobPropertyContext context) {
        return Util.immutableCopy(that, context, EXECUTION_BUILD_TRANSFORMER);
    }

    public static List<ExecutionImpl> immutableCopyExecutionsPartition(final List<ExecutionImpl> that, final PartitionPropertyContext context) {
        return Util.immutableCopy(that, context, EXECUTION_PARTITION_TRANSFORMER);
    }
}
