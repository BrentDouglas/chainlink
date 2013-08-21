package io.machinecode.nock.core.factory.execution;

import io.machinecode.nock.core.descriptor.execution.DecisionImpl;
import io.machinecode.nock.core.descriptor.execution.FlowImpl;
import io.machinecode.nock.core.descriptor.execution.SplitImpl;
import io.machinecode.nock.core.descriptor.execution.StepImpl;
import io.machinecode.nock.core.descriptor.partition.MapperImpl;
import io.machinecode.nock.core.descriptor.partition.PlanImpl;
import io.machinecode.nock.core.descriptor.task.BatchletImpl;
import io.machinecode.nock.core.descriptor.task.ChunkImpl;
import io.machinecode.nock.core.expression.JobParameterContext;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.util.Util;
import io.machinecode.nock.core.util.Util.NextExpressionTransformer;
import io.machinecode.nock.core.work.execution.DecisionWork;
import io.machinecode.nock.core.work.execution.ExecutionWork;
import io.machinecode.nock.core.work.execution.FlowWork;
import io.machinecode.nock.core.work.execution.SplitWork;
import io.machinecode.nock.core.work.execution.StepWork;
import io.machinecode.nock.core.work.partition.MapperWork;
import io.machinecode.nock.core.work.partition.PartitionWork;
import io.machinecode.nock.core.work.partition.PlanWork;
import io.machinecode.nock.core.work.task.BatchletWork;
import io.machinecode.nock.core.work.task.ChunkWork;
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

    private static final NextExpressionTransformer<Execution, Execution, JobPropertyContext> EXECUTION_BUILD_TRANSFORMER = new NextExpressionTransformer<Execution, Execution, JobPropertyContext>() {
        @Override
        public Execution transform(final Execution that, final Execution next, final JobPropertyContext context) {
            if (that instanceof Flow) {
                return FlowFactory.INSTANCE.produceDescriptor((Flow) that, next, context);
            } else if (that instanceof Split) {
                return SplitFactory.INSTANCE.produceDescriptor((Split) that, next, context);
            } else if (that instanceof Step) {
                final Task task = ((Step) that).getTask();
                final Partition partition = ((Step) that).getPartition();
                final Strategy strategy = partition == null ? null : partition.getStrategy();
                //There is no real reason for these selections on null values
                if (task == null || task instanceof Batchlet) {
                    if (strategy == null || strategy instanceof Mapper) {
                        return BatchletMapperStepFactory.INSTANCE.produceDescriptor((Step<Batchlet, Mapper>) that, next, context);
                    } else if (strategy instanceof Plan) {
                        return BatchletPlanStepFactory.INSTANCE.produceDescriptor((Step<Batchlet, Plan>) that, next, context);
                    }
                } else if (task instanceof Chunk) {
                    if (strategy == null || strategy instanceof Mapper) {
                        return ChunkMapperStepFactory.INSTANCE.produceDescriptor((Step<Chunk, Mapper>) that, next, context);
                    } else if (strategy instanceof Plan) {
                        return ChunkPlanStepFactory.INSTANCE.produceDescriptor((Step<Chunk, Plan>) that, next, context);
                    }
                }
            } else if (that instanceof Decision) {
                return DecisionFactory.INSTANCE.produceDescriptor((Decision) that, context);
            }
            return null;
        }
    };

    private static final NextExpressionTransformer<Execution, ExecutionWork, JobParameterContext> EXECUTION_EXECUTION_TRANSFORMER = new NextExpressionTransformer<Execution, ExecutionWork, JobParameterContext>() {
        @Override
        public ExecutionWork transform(final Execution that, final Execution next, final JobParameterContext context) {
            if (that instanceof FlowImpl) {
                return FlowFactory.INSTANCE.produceExecution((FlowImpl) that, next, context);
            } else if (that instanceof SplitImpl) {
                return SplitFactory.INSTANCE.produceExecution((SplitImpl) that, next, context);
            } else if (that instanceof Step) {
                final Task task = ((StepImpl) that).getTask();
                final Partition partition = ((StepImpl) that).getPartition();
                final Strategy strategy = partition == null ? null : partition.getStrategy();
                //There is no real reason for these selections on null values
                if (task == null || task instanceof BatchletImpl) {
                    if (strategy == null || strategy instanceof MapperImpl) {
                        return BatchletMapperStepFactory.INSTANCE.produceExecution((StepImpl<BatchletImpl, MapperImpl>) that, next, context);
                    } else if (strategy instanceof PlanImpl) {
                        return BatchletPlanStepFactory.INSTANCE.produceExecution((StepImpl<BatchletImpl, PlanImpl>) that, next, context);
                    }
                } else if (task instanceof ChunkImpl) {
                    if (strategy == null || strategy instanceof MapperImpl) {
                        return ChunkMapperStepFactory.INSTANCE.produceExecution((StepImpl<ChunkImpl, MapperImpl>) that, next, context);
                    } else if (strategy instanceof PlanImpl) {
                        return ChunkPlanStepFactory.INSTANCE.produceExecution((StepImpl<ChunkImpl, PlanImpl>) that, next, context);
                    }
                }
            } else if (that instanceof DecisionImpl) {
                return DecisionFactory.INSTANCE.produceExecution((DecisionImpl) that, context);
            }
            return null;
        }
    };

    private static final NextExpressionTransformer<ExecutionWork, ExecutionWork, PartitionPropertyContext> EXECUTION_PARTITION_TRANSFORMER = new NextExpressionTransformer<ExecutionWork, ExecutionWork, PartitionPropertyContext>() {
        @Override
        public ExecutionWork transform(final ExecutionWork that, final ExecutionWork next, final PartitionPropertyContext context) {
            if (that instanceof FlowWork) {
                return FlowFactory.INSTANCE.producePartitioned((FlowWork) that, next, context);
            } else if (that instanceof SplitWork) {
                return SplitFactory.INSTANCE.producePartitioned((SplitWork) that, next, context);
            } else if (that instanceof StepWork) {
                final Task task = ((StepWork) that).getTask();
                final Partition partition = ((StepWork) that).getPartition();
                final Strategy strategy = partition == null ? null : partition.getStrategy();
                //There is no real reason for these selections on null values
                if (task == null || task instanceof Batchlet) {
                    if (strategy == null || strategy instanceof Mapper) {
                        return BatchletMapperStepFactory.INSTANCE.producePartitioned((StepWork<BatchletWork, MapperWork, PartitionWork<MapperWork>>) that, next, context);
                    } else if (strategy instanceof Plan) {
                        return BatchletPlanStepFactory.INSTANCE.producePartitioned((StepWork<BatchletWork, PlanWork, PartitionWork<PlanWork>>) that, next, context);
                    }
                } else if (task instanceof Chunk) {
                    if (strategy == null || strategy instanceof Mapper) {
                        return ChunkMapperStepFactory.INSTANCE.producePartitioned((StepWork<ChunkWork, MapperWork, PartitionWork<MapperWork>>) that, next, context);
                    } else if (strategy instanceof Plan) {
                        return ChunkPlanStepFactory.INSTANCE.producePartitioned((StepWork<ChunkWork, PlanWork, PartitionWork<PlanWork>>) that, next, context);
                    }
                }
            } else if (that instanceof DecisionWork) {
                return DecisionFactory.INSTANCE.producePartitioned((DecisionWork) that, context);
            }
            return null;
        }
    };


    public static List<Execution> immutableCopyExecutionsDescriptor(final List<? extends Execution> that, final JobPropertyContext context) {
        return Util.immutableCopy(that, context, EXECUTION_BUILD_TRANSFORMER);
    }

    public static List<ExecutionWork> immutableCopyExecutionsExecution(final List<? extends Execution> that, final JobParameterContext context) {
        return Util.immutableCopy(that, context, EXECUTION_EXECUTION_TRANSFORMER);
    }

    public static List<ExecutionWork> immutableCopyExecutionsPartition(final List<ExecutionWork> that, final PartitionPropertyContext context) {
        return Util.immutableCopy(that, context, EXECUTION_PARTITION_TRANSFORMER);
    }
}
