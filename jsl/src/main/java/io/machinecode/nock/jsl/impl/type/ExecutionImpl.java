package io.machinecode.nock.jsl.impl.type;

import io.machinecode.nock.jsl.api.Batchlet;
import io.machinecode.nock.jsl.api.Part;
import io.machinecode.nock.jsl.api.chunk.Chunk;
import io.machinecode.nock.jsl.api.execution.Execution;
import io.machinecode.nock.jsl.api.partition.Mapper;
import io.machinecode.nock.jsl.api.partition.PartitionMapper;
import io.machinecode.nock.jsl.api.partition.PartitionPlan;
import io.machinecode.nock.jsl.api.execution.Decision;
import io.machinecode.nock.jsl.api.execution.Flow;
import io.machinecode.nock.jsl.api.execution.Split;
import io.machinecode.nock.jsl.api.execution.Step;
import io.machinecode.nock.jsl.impl.Util;
import io.machinecode.nock.jsl.impl.Util.Transformer;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ExecutionImpl implements Execution {

    private static final Transformer<Execution> EXECUTION_TRANSFORMER = new Transformer<Execution>() {
        @Override
        public Execution transform(final Execution that) {
            if (that instanceof Flow) {
                return new FlowImpl((Flow) that);
            } else if (that instanceof Split) {
                return new SplitImpl((Split) that);
            } else if (that instanceof Step) {
                final Part part = ((Step) that).getPart();
                final Mapper mapper = ((Step) that).getPartition().getMapper();
                if (part instanceof Batchlet) {
                    if (mapper instanceof PartitionMapper) {
                        return new BatchletMapperStepImpl((Step<Batchlet, PartitionMapper>) that);
                    } else if (mapper instanceof PartitionPlan) {
                        return new BatchletPlanStepImpl((Step<Batchlet, PartitionPlan>) that);
                    }
                } else if (part instanceof Chunk) {
                    if (mapper instanceof PartitionMapper) {
                        return new ChunkMapperStepImpl((Step<Chunk, PartitionMapper>) that);
                    } else if (mapper instanceof PartitionPlan) {
                        return new ChunkPlanStepImpl((Step<Chunk, PartitionPlan>) that);
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
