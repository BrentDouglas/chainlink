package io.machinecode.nock.core.model.execution;

import io.machinecode.nock.core.loader.TypedArtifactReference;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.transition.TransitionImpl;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.element.execution.Decision;
import io.machinecode.nock.spi.transport.Plan;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.util.Message;
import io.machinecode.nock.spi.work.ExecutionWork;
import org.jboss.logging.Logger;

import javax.batch.api.Decider;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class DecisionImpl extends ExecutionImpl implements Decision {

    private static final Logger log = Logger.getLogger(DecisionImpl.class);

    private final List<TransitionImpl> transitions;
    private final PropertiesImpl properties;
    private final TypedArtifactReference<Decider> ref;

    public DecisionImpl(final String id, final TypedArtifactReference<Decider> ref, final PropertiesImpl properties, final List<TransitionImpl> transitions) {
        super(id);
        this.transitions = transitions;
        this.properties = properties;
        this.ref = ref;
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
        final Decider decider = this.ref.load(transport, context, this);

        log.debugf(Message.get("decision.decide"), context.getJobExecutionId(), this.id, this.ref.ref());
        final String exitStatus = decider.decide(transport.getRepository().getStepExecutions(context.getStepExecutionIds()));
        context.getJobContext().setExitStatus(exitStatus); //TODO Really?
        final ExecutionWork execution = this.transition(transport, context, this.transitions, null);
        if (execution != null) {
            return execution.plan(transport, context);
        }
        return null;
    }
}
