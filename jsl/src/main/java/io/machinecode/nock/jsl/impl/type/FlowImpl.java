package io.machinecode.nock.jsl.impl.type;

import io.machinecode.nock.jsl.api.Batchlet;
import io.machinecode.nock.jsl.api.Part;
import io.machinecode.nock.jsl.api.chunk.Chunk;
import io.machinecode.nock.jsl.api.partition.Mapper;
import io.machinecode.nock.jsl.api.partition.PartitionMapper;
import io.machinecode.nock.jsl.api.partition.PartitionPlan;
import io.machinecode.nock.jsl.api.transition.End;
import io.machinecode.nock.jsl.api.transition.Fail;
import io.machinecode.nock.jsl.api.transition.Next;
import io.machinecode.nock.jsl.api.transition.Stop;
import io.machinecode.nock.jsl.api.transition.Transition;
import io.machinecode.nock.jsl.api.type.Decision;
import io.machinecode.nock.jsl.api.type.Flow;
import io.machinecode.nock.jsl.api.type.Split;
import io.machinecode.nock.jsl.api.type.Step;
import io.machinecode.nock.jsl.api.type.Type;
import io.machinecode.nock.jsl.impl.transition.EndImpl;
import io.machinecode.nock.jsl.impl.transition.FailImpl;
import io.machinecode.nock.jsl.impl.transition.NextImpl;
import io.machinecode.nock.jsl.impl.transition.StopImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FlowImpl extends TypeImpl implements Flow {

    private final String next;
    private final List<Type> types;
    private final List<Transition> transitions;

    public FlowImpl(final Flow that) {
        super(that);
        this.next = that.getNext();
        this.types = new ArrayList<Type>(that.getTypes().size());
        for (final Type type : that.getTypes()) {
            if (type instanceof Flow) {
                this.types.add(new FlowImpl((Flow)type));
            } else if (type instanceof Split) {
                this.types.add(new SplitImpl((Split)type));
            } else if (type instanceof Step) {
                final Part part = ((Step) type).getPart();
                final Mapper mapper = ((Step) type).getPartition().getMapper();
                if (part instanceof Batchlet) {
                    if (mapper instanceof PartitionMapper) {
                        this.types.add(new BatchletMapperStepImpl((Step<Batchlet, PartitionMapper>)type));
                    } else if (mapper instanceof PartitionPlan) {
                        this.types.add(new BatchletPlanStepImpl((Step<Batchlet, PartitionPlan>)type));
                    }
                } else if (part instanceof Chunk) {
                    if (mapper instanceof PartitionMapper) {
                        this.types.add(new ChunkMapperStepImpl((Step<Chunk, PartitionMapper>)type));
                    } else if (mapper instanceof PartitionPlan) {
                        this.types.add(new ChunkPlanStepImpl((Step<Chunk, PartitionPlan>)type));
                    }
                }
            } else if (type instanceof Decision) {
                this.types.add(new DecisionImpl((Decision)type));
            }
        }
        this.transitions = new ArrayList<Transition>(that.getTransitions().size());
        for (final Transition transition : that.getTransitions()) {
            if (transition instanceof End) {
                this.transitions.add(new EndImpl((End)transition));
            } else if (transition instanceof Fail) {
                this.transitions.add(new FailImpl((Fail)transition));
            } else if (transition instanceof Next) {
                this.transitions.add(new NextImpl((Next)transition));
            } else if (transition instanceof Stop) {
                this.transitions.add(new StopImpl((Stop)transition));
            }
        }
    }

    @Override
    public String getNext() {
        return this.next;
    }

    @Override
    public List<Type> getTypes() {
        return this.types;
    }

    @Override
    public List<Transition> getTransitions() {
        return this.transitions;
    }
}
