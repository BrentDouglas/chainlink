package io.machinecode.nock.jsl.impl.execution;

import io.machinecode.nock.jsl.api.partition.Plan;
import io.machinecode.nock.jsl.api.task.Batchlet;
import io.machinecode.nock.jsl.api.task.Task;
import io.machinecode.nock.jsl.api.task.Chunk;
import io.machinecode.nock.jsl.api.execution.Decision;
import io.machinecode.nock.jsl.api.execution.Execution;
import io.machinecode.nock.jsl.api.execution.Flow;
import io.machinecode.nock.jsl.api.execution.Split;
import io.machinecode.nock.jsl.api.execution.Step;
import io.machinecode.nock.jsl.api.partition.Strategy;
import io.machinecode.nock.jsl.api.partition.Partition;
import io.machinecode.nock.jsl.api.partition.Mapper;
import io.machinecode.nock.jsl.impl.Util;
import io.machinecode.nock.jsl.impl.Util.NextTransformer;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ExecutionImpl implements Execution {

    private static final NextTransformer<Execution> EXECUTION_TRANSFORMER = new NextTransformer<Execution>() {
        @Override
        public Execution transform(final Execution that, final Execution next) {
            if (that instanceof Flow) {
                return new FlowImpl((Flow) that, next);
            } else if (that instanceof Split) {
                return new SplitImpl((Split) that, next);
            } else if (that instanceof Step) {
                final Task task = ((Step) that).getTask();
                final Partition partition = ((Step) that).getPartition();
                final Strategy strategy = partition == null ? null : partition.getStrategy();
                //There is no real reason for these selections on null values
                if (task == null || task instanceof Batchlet) {
                    if (strategy == null || strategy instanceof Mapper) {
                        return new BatchletMapperStepImpl((Step<Batchlet, Mapper>) that, next);
                    } else if (strategy instanceof Plan) {
                        return new BatchletPlanStepImpl((Step<Batchlet, Plan>) that, next);
                    }
                } else if (task instanceof Chunk) {
                    if (strategy == null || strategy instanceof Mapper) {
                        return new ChunkMapperStepImpl((Step<Chunk, Mapper>) that, next);
                    } else if (strategy instanceof Plan) {
                        return new ChunkPlanStepImpl((Step<Chunk, Plan>) that, next);
                    }
                }
            } else if (that instanceof Decision) {
                return new DecisionImpl((Decision) that);
            }
            return null;
        }
    };

    private final String id;

    public ExecutionImpl(final Execution that) {
        this.id = that.getId();
    }

    @Override
    public String getId() {
        return this.id;
    }

    public static List<Execution> immutableCopyExecutions(final List<? extends Execution> that) {
        return Util.immutableCopy(that, EXECUTION_TRANSFORMER);
    }
}
