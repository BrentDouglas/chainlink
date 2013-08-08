package io.machinecode.nock.core.factory.execution;

import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.util.Util;
import io.machinecode.nock.core.util.Util.ExpressionTransformer;
import io.machinecode.nock.core.util.Util.NextExpressionTransformer;
import io.machinecode.nock.core.util.Util.ParametersTransformer;
import io.machinecode.nock.jsl.api.execution.Decision;
import io.machinecode.nock.jsl.api.execution.Execution;
import io.machinecode.nock.jsl.api.execution.Flow;
import io.machinecode.nock.jsl.api.execution.Split;
import io.machinecode.nock.jsl.api.execution.Step;
import io.machinecode.nock.jsl.api.partition.Mapper;
import io.machinecode.nock.jsl.api.partition.Partition;
import io.machinecode.nock.jsl.api.partition.Plan;
import io.machinecode.nock.jsl.api.partition.Strategy;
import io.machinecode.nock.jsl.api.task.Batchlet;
import io.machinecode.nock.jsl.api.task.Chunk;
import io.machinecode.nock.jsl.api.task.Task;

import java.util.List;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class Executions {

    private static final NextExpressionTransformer<Execution, Execution> EXECUTION_BUILD_TRANSFORMER = new NextExpressionTransformer<Execution, Execution>() {
        @Override
        public Execution transform(final Execution that, final Execution next, final JobPropertyContext context) {
            if (that instanceof Flow) {
                return FlowFactory.INSTANCE.produceBuildTime((Flow) that, next, context);
            } else if (that instanceof Split) {
                return SplitFactory.INSTANCE.produceBuildTime((Split) that, next, context);
            } else if (that instanceof Step) {
                final Task task = ((Step) that).getTask();
                final Partition partition = ((Step) that).getPartition();
                final Strategy strategy = partition == null ? null : partition.getStrategy();
                //There is no real reason for these selections on null values
                if (task == null || task instanceof Batchlet) {
                    if (strategy == null || strategy instanceof Mapper) {
                        return BatchletMapperStepFactory.INSTANCE.produceBuildTime((Step<Batchlet, Mapper>) that, next, context);
                    } else if (strategy instanceof Plan) {
                        return BatchletPlanStepFactory.INSTANCE.produceBuildTime((Step<Batchlet, Plan>) that, next, context);
                    }
                } else if (task instanceof Chunk) {
                    if (strategy == null || strategy instanceof Mapper) {
                        return ChunkMapperStepFactory.INSTANCE.produceBuildTime((Step<Chunk, Mapper>) that, next, context);
                    } else if (strategy instanceof Plan) {
                        return ChunkPlanStepFactory.INSTANCE.produceBuildTime((Step<Chunk, Plan>) that, next, context);
                    }
                }
            } else if (that instanceof Decision) {
                return DecisionFactory.INSTANCE.produceBuildTime((Decision) that, context);
            }
            return null;
        }
    };

    private static final ParametersTransformer<Execution, Execution> EXECUTION_START_TRANSFORMER = new ParametersTransformer<Execution, Execution>() {
        @Override
        public Execution transform(final Execution that, final Properties parameters) {
            if (that instanceof Flow) {
                return FlowFactory.INSTANCE.produceStartTime((Flow) that, parameters);
            } else if (that instanceof Split) {
                return SplitFactory.INSTANCE.produceStartTime((Split) that, parameters);
            } else if (that instanceof Step) {
                final Task task = ((Step) that).getTask();
                final Partition partition = ((Step) that).getPartition();
                final Strategy strategy = partition == null ? null : partition.getStrategy();
                //There is no real reason for these selections on null values
                if (task == null || task instanceof Batchlet) {
                    if (strategy == null || strategy instanceof Mapper) {
                        return BatchletMapperStepFactory.INSTANCE.produceStartTime((Step<Batchlet, Mapper>) that, parameters);
                    } else if (strategy instanceof Plan) {
                        return BatchletPlanStepFactory.INSTANCE.produceStartTime((Step<Batchlet, Plan>) that, parameters);
                    }
                } else if (task instanceof Chunk) {
                    if (strategy == null || strategy instanceof Mapper) {
                        return ChunkMapperStepFactory.INSTANCE.produceStartTime((Step<Chunk, Mapper>) that, parameters);
                    } else if (strategy instanceof Plan) {
                        return ChunkPlanStepFactory.INSTANCE.produceStartTime((Step<Chunk, Plan>) that, parameters);
                    }
                }
            } else if (that instanceof Decision) {
                return DecisionFactory.INSTANCE.produceStartTime((Decision) that, parameters);
            }
            return null;
        }
    };

    private static final ExpressionTransformer<Execution, Execution> EXECUTION_RUN_TRANSFORMER = new ExpressionTransformer<Execution, Execution>() {
        @Override
        public Execution transform(final Execution that, final JobPropertyContext context) {
            if (that instanceof Flow) {
                return FlowFactory.INSTANCE.producePartitionTime((Flow) that, context);
            } else if (that instanceof Split) {
                return SplitFactory.INSTANCE.producePartitionTime((Split) that, context);
            } else if (that instanceof Step) {
                final Task task = ((Step) that).getTask();
                final Partition partition = ((Step) that).getPartition();
                final Strategy strategy = partition == null ? null : partition.getStrategy();
                //There is no real reason for these selections on null values
                if (task == null || task instanceof Batchlet) {
                    if (strategy == null || strategy instanceof Mapper) {
                        return BatchletMapperStepFactory.INSTANCE.producePartitionTime((Step<Batchlet, Mapper>) that, context);
                    } else if (strategy instanceof Plan) {
                        return BatchletPlanStepFactory.INSTANCE.producePartitionTime((Step<Batchlet, Plan>) that, context);
                    }
                } else if (task instanceof Chunk) {
                    if (strategy == null || strategy instanceof Mapper) {
                        return ChunkMapperStepFactory.INSTANCE.producePartitionTime((Step<Chunk, Mapper>) that, context);
                    } else if (strategy instanceof Plan) {
                        return ChunkPlanStepFactory.INSTANCE.producePartitionTime((Step<Chunk, Plan>) that, context);
                    }
                }
            } else if (that instanceof Decision) {
                return DecisionFactory.INSTANCE.producePartitionTime((Decision) that, context);
            }
            return null;
        }
    };


    public static List<Execution> immutableCopyExecutionsBuildTime(final List<? extends Execution> that, final JobPropertyContext context) {
        return Util.immutableCopy(that, context, EXECUTION_BUILD_TRANSFORMER);
    }

    public static List<Execution> immutableCopyExecutionsStartTime(final List<? extends Execution> that, final Properties parameters) {
        return Util.immutableCopy(that, parameters, EXECUTION_START_TRANSFORMER);
    }

    public static List<Execution> immutableCopyExecutionsPartitionTime(final List<? extends Execution> that, final JobPropertyContext context) {
        return Util.immutableCopy(that, context, EXECUTION_RUN_TRANSFORMER);
    }
}
