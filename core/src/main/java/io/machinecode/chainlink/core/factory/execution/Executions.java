package io.machinecode.chainlink.core.factory.execution;

import io.machinecode.chainlink.core.model.execution.DecisionImpl;
import io.machinecode.chainlink.core.model.execution.ExecutionImpl;
import io.machinecode.chainlink.core.model.execution.FlowImpl;
import io.machinecode.chainlink.core.model.execution.SplitImpl;
import io.machinecode.chainlink.core.model.execution.StepImpl;
import io.machinecode.chainlink.core.model.partition.MapperImpl;
import io.machinecode.chainlink.core.model.partition.PlanImpl;
import io.machinecode.chainlink.core.model.task.BatchletImpl;
import io.machinecode.chainlink.core.model.task.ChunkImpl;
import io.machinecode.chainlink.core.util.Util;
import io.machinecode.chainlink.core.util.Util.ExpressionTransformer;
import io.machinecode.chainlink.spi.element.execution.Decision;
import io.machinecode.chainlink.spi.element.execution.Execution;
import io.machinecode.chainlink.spi.element.execution.Flow;
import io.machinecode.chainlink.spi.element.execution.Split;
import io.machinecode.chainlink.spi.element.execution.Step;
import io.machinecode.chainlink.spi.element.partition.Mapper;
import io.machinecode.chainlink.spi.element.partition.Partition;
import io.machinecode.chainlink.spi.element.partition.Plan;
import io.machinecode.chainlink.spi.element.partition.Strategy;
import io.machinecode.chainlink.spi.element.task.Batchlet;
import io.machinecode.chainlink.spi.element.task.Chunk;
import io.machinecode.chainlink.spi.element.task.Task;
import io.machinecode.chainlink.spi.factory.JobPropertyContext;
import io.machinecode.chainlink.spi.factory.PropertyContext;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class Executions {

    @SuppressWarnings("unchecked")
    private static final ExpressionTransformer<Execution, ExecutionImpl, JobPropertyContext> EXECUTION_BUILD_TRANSFORMER = new ExpressionTransformer<Execution, ExecutionImpl, JobPropertyContext>() {
        @Override
        public ExecutionImpl transform(final Execution that, final JobPropertyContext context) {
            if (that instanceof Flow) {
                return FlowFactory.INSTANCE.produceExecution((Flow) that,  context);
            } else if (that instanceof Split) {
                return SplitFactory.INSTANCE.produceExecution((Split) that, context);
            } else if (that instanceof Step) {
                final Task task = ((Step) that).getTask();
                final Partition partition = ((Step) that).getPartition();
                final Strategy strategy = partition == null ? null : partition.getStrategy();
                //There is no real reason for these selections on null values
                if (task == null || task instanceof Batchlet) {
                    if (strategy == null || strategy instanceof Mapper) {
                        return BatchletMapperStepFactory.INSTANCE.produceExecution((Step<Batchlet, Mapper>) that,  context);
                    } else if (strategy instanceof Plan) {
                        return BatchletPlanStepFactory.INSTANCE.produceExecution((Step<Batchlet, Plan>) that, context);
                    }
                } else if (task instanceof Chunk) {
                    if (strategy == null || strategy instanceof Mapper) {
                        return ChunkMapperStepFactory.INSTANCE.produceExecution((Step<Chunk, Mapper>) that,  context);
                    } else if (strategy instanceof Plan) {
                        return ChunkPlanStepFactory.INSTANCE.produceExecution((Step<Chunk, Plan>) that, context);
                    }
                }
            } else if (that instanceof Decision) {
                return DecisionFactory.INSTANCE.produceExecution((Decision) that, context);
            }
            return null;
        }
    };

    @SuppressWarnings("unchecked")
    private static final ExpressionTransformer<ExecutionImpl, ExecutionImpl, PropertyContext> EXECUTION_PARTITION_TRANSFORMER = new ExpressionTransformer<ExecutionImpl, ExecutionImpl, PropertyContext>() {
        @Override
        public ExecutionImpl transform(final ExecutionImpl that, final PropertyContext context) {
            if (that instanceof FlowImpl) {
                return FlowFactory.INSTANCE.producePartitioned((FlowImpl) that, context);
            } else if (that instanceof SplitImpl) {
                return SplitFactory.INSTANCE.producePartitioned((SplitImpl) that, context);
            } else if (that instanceof StepImpl) {
                final Task task = ((StepImpl) that).getTask();
                final Partition partition = ((StepImpl) that).getPartition();
                final Strategy strategy = partition == null ? null : partition.getStrategy();
                //There is no real reason for these selections on null values
                if (task == null || task instanceof Batchlet) {
                    if (strategy == null || strategy instanceof Mapper) {
                        return BatchletMapperStepFactory.INSTANCE.producePartitioned((StepImpl<BatchletImpl, MapperImpl>) that, context);
                    } else if (strategy instanceof Plan) {
                        return BatchletPlanStepFactory.INSTANCE.producePartitioned((StepImpl<BatchletImpl, PlanImpl>) that, context);
                    }
                } else if (task instanceof Chunk) {
                    if (strategy == null || strategy instanceof Mapper) {
                        return ChunkMapperStepFactory.INSTANCE.producePartitioned((StepImpl<ChunkImpl, MapperImpl>) that, context);
                    } else if (strategy instanceof Plan) {
                        return ChunkPlanStepFactory.INSTANCE.producePartitioned((StepImpl<ChunkImpl, PlanImpl>) that, context);
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

    public static List<ExecutionImpl> immutableCopyExecutionsPartition(final List<ExecutionImpl> that, final PropertyContext context) {
        return Util.immutableCopy(that, context, EXECUTION_PARTITION_TRANSFORMER);
    }
}
