package io.machinecode.nock.jsl.impl.execution;

import io.machinecode.nock.jsl.api.Batchlet;
import io.machinecode.nock.jsl.api.Part;
import io.machinecode.nock.jsl.api.chunk.Chunk;
import io.machinecode.nock.jsl.api.execution.Decision;
import io.machinecode.nock.jsl.api.execution.Execution;
import io.machinecode.nock.jsl.api.execution.Flow;
import io.machinecode.nock.jsl.api.execution.Split;
import io.machinecode.nock.jsl.api.execution.Step;
import io.machinecode.nock.jsl.api.partition.Mapper;
import io.machinecode.nock.jsl.api.partition.Partition;
import io.machinecode.nock.jsl.api.partition.PartitionMapper;
import io.machinecode.nock.jsl.api.partition.PartitionPlan;
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
                final Part part = ((Step) that).getPart();
                final Partition partition = ((Step) that).getPartition();
                final Mapper mapper = partition == null ? null : partition.getMapper();
                //There is no real reason for these selections on null values
                if (part == null || part instanceof Batchlet) {
                    if (mapper == null || mapper instanceof PartitionMapper) {
                        return new BatchletMapperStepImpl((Step<Batchlet, PartitionMapper>) that, next);
                    } else if (mapper instanceof PartitionPlan) {
                        return new BatchletPlanStepImpl((Step<Batchlet, PartitionPlan>) that, next);
                    }
                } else if (part instanceof Chunk) {
                    if (mapper == null || mapper instanceof PartitionMapper) {
                        return new ChunkMapperStepImpl((Step<Chunk, PartitionMapper>) that, next);
                    } else if (mapper instanceof PartitionPlan) {
                        return new ChunkPlanStepImpl((Step<Chunk, PartitionPlan>) that, next);
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
