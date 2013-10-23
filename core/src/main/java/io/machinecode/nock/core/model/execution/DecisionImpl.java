package io.machinecode.nock.core.model.execution;

import io.machinecode.nock.core.loader.TypedArtifactReference;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.transition.TransitionImpl;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.element.execution.Decision;
import io.machinecode.nock.spi.inject.InjectionContext;
import io.machinecode.nock.spi.transport.Plan;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.work.ExecutionWork;

import javax.batch.api.Decider;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class DecisionImpl extends ExecutionImpl implements Decision {

    private final List<TransitionImpl> transitions;
    private final PropertiesImpl properties;
    private final TypedArtifactReference<Decider> ref;

    public DecisionImpl(final String id, final String ref, final PropertiesImpl properties, final List<TransitionImpl> transitions) {
        super(id);
        this.transitions = transitions;
        this.properties = properties;
        this.ref = new TypedArtifactReference<Decider>(ref, Decider.class);
    }

    @Override
    public List<TransitionImpl> getTransitions() {
        return this.transitions;
    }

    @Override
    public String getRef() {
        return this.ref.ref();
    }

    @Override
    public PropertiesImpl getProperties() {
        return this.properties;
    }

    @Override
    public String element() {
        return ELEMENT;
    }

    @Override
    public Plan run(final Transport transport, final Context context) throws Exception {
        final InjectionContext ic = transport.createInjectionContext(context);
        final ClassLoader classLoader = ic.getClassLoader();
        final Decider decider = this.ref.load(classLoader, ic.getArtifactLoader());

        final String exitStatus = decider.decide(transport.getRepository().getStepExecutions(context.getStepExecutionIds()));
        context.getJobContext().setExitStatus(exitStatus);
        final ExecutionWork execution = this.transitionOrSetStatus(transport, context, this.transitions, null);
        if (execution != null) {
            //return transport.execute(context.getJobExecutionId(), this, execution.plan(transport, context));
            return execution.plan(transport, context);
        }
        //return new DeferredImpl<Void>();
        return null;
    }
}
