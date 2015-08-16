/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @authors tag. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.machinecode.chainlink.core.factory.execution;

import io.machinecode.chainlink.core.expression.JobPropertyContext;
import io.machinecode.chainlink.core.expression.PartitionPropertyContext;
import io.machinecode.chainlink.core.jsl.impl.execution.DecisionImpl;
import io.machinecode.chainlink.core.jsl.impl.execution.ExecutionImpl;
import io.machinecode.chainlink.core.jsl.impl.execution.FlowImpl;
import io.machinecode.chainlink.core.jsl.impl.execution.SplitImpl;
import io.machinecode.chainlink.core.jsl.impl.execution.StepImpl;
import io.machinecode.chainlink.core.jsl.impl.partition.MapperImpl;
import io.machinecode.chainlink.core.jsl.impl.partition.PlanImpl;
import io.machinecode.chainlink.core.jsl.impl.task.BatchletImpl;
import io.machinecode.chainlink.core.jsl.impl.task.ChunkImpl;
import io.machinecode.chainlink.core.util.Copy;
import io.machinecode.chainlink.core.util.Copy.ExpressionTransformer;
import io.machinecode.chainlink.spi.jsl.execution.Decision;
import io.machinecode.chainlink.spi.jsl.execution.Execution;
import io.machinecode.chainlink.spi.jsl.execution.Flow;
import io.machinecode.chainlink.spi.jsl.execution.Split;
import io.machinecode.chainlink.spi.jsl.execution.Step;
import io.machinecode.chainlink.spi.jsl.partition.Mapper;
import io.machinecode.chainlink.spi.jsl.partition.Partition;
import io.machinecode.chainlink.spi.jsl.partition.Plan;
import io.machinecode.chainlink.spi.jsl.partition.Strategy;
import io.machinecode.chainlink.spi.jsl.task.Batchlet;
import io.machinecode.chainlink.spi.jsl.task.Chunk;
import io.machinecode.chainlink.spi.jsl.task.Task;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class Executions {

    @SuppressWarnings("unchecked")
    private static final ExpressionTransformer<Execution, ExecutionImpl, JobPropertyContext> EXECUTION_BUILD_TRANSFORMER = new ExpressionTransformer<Execution, ExecutionImpl, JobPropertyContext>() {
        @Override
        public ExecutionImpl transform(final Execution that, final JobPropertyContext context) {
            if (that instanceof Flow) {
                return FlowFactory.produceExecution((Flow) that,  context);
            } else if (that instanceof Split) {
                return SplitFactory.produceExecution((Split) that, context);
            } else if (that instanceof Step) {
                final Task task = ((Step) that).getTask();
                final Partition partition = ((Step) that).getPartition();
                final Strategy strategy = partition == null ? null : partition.getStrategy();
                //There is no real reason for these selections on null values
                if (task == null || task instanceof Batchlet) {
                    if (strategy == null || strategy instanceof Mapper) {
                        return BatchletMapperStepFactory.produceExecution((Step<Batchlet, Mapper>) that,  context);
                    } else if (strategy instanceof Plan) {
                        return BatchletPlanStepFactory.produceExecution((Step<Batchlet, Plan>) that, context);
                    }
                } else if (task instanceof Chunk) {
                    if (strategy == null || strategy instanceof Mapper) {
                        return ChunkMapperStepFactory.produceExecution((Step<Chunk, Mapper>) that,  context);
                    } else if (strategy instanceof Plan) {
                        return ChunkPlanStepFactory.produceExecution((Step<Chunk, Plan>) that, context);
                    }
                }
            } else if (that instanceof Decision) {
                return DecisionFactory.produceExecution((Decision) that, context);
            }
            return null;
        }
    };

    @SuppressWarnings("unchecked")
    private static final ExpressionTransformer<ExecutionImpl, ExecutionImpl, PartitionPropertyContext> EXECUTION_PARTITION_TRANSFORMER = new ExpressionTransformer<ExecutionImpl, ExecutionImpl, PartitionPropertyContext>() {
        @Override
        public ExecutionImpl transform(final ExecutionImpl that, final PartitionPropertyContext context) {
            if (that instanceof FlowImpl) {
                return FlowFactory.producePartitioned((FlowImpl) that, context);
            } else if (that instanceof SplitImpl) {
                return SplitFactory.producePartitioned((SplitImpl) that, context);
            } else if (that instanceof StepImpl) {
                final Task task = ((StepImpl) that).getTask();
                final Partition partition = ((StepImpl) that).getPartition();
                final Strategy strategy = partition == null ? null : partition.getStrategy();
                //There is no real reason for these selections on null values
                if (task == null || task instanceof Batchlet) {
                    if (strategy == null || strategy instanceof Mapper) {
                        return BatchletMapperStepFactory.producePartitioned((StepImpl<BatchletImpl, MapperImpl>) that, context);
                    } else if (strategy instanceof Plan) {
                        return BatchletPlanStepFactory.producePartitioned((StepImpl<BatchletImpl, PlanImpl>) that, context);
                    }
                } else if (task instanceof Chunk) {
                    if (strategy == null || strategy instanceof Mapper) {
                        return ChunkMapperStepFactory.producePartitioned((StepImpl<ChunkImpl, MapperImpl>) that, context);
                    } else if (strategy instanceof Plan) {
                        return ChunkPlanStepFactory.producePartitioned((StepImpl<ChunkImpl, PlanImpl>) that, context);
                    }
                }
            } else if (that instanceof DecisionImpl) {
                return DecisionFactory.producePartitioned((DecisionImpl) that, context);
            }
            return null;
        }
    };


    public static List<ExecutionImpl> immutableCopyExecutionsDescriptor(final List<? extends Execution> that, final JobPropertyContext context) {
        return Copy.immutableCopy(that, context, EXECUTION_BUILD_TRANSFORMER);
    }

    public static List<ExecutionImpl> immutableCopyExecutionsPartition(final List<ExecutionImpl> that, final PartitionPropertyContext context) {
        return Copy.immutableCopy(that, context, EXECUTION_PARTITION_TRANSFORMER);
    }
}
