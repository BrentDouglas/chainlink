package io.machinecode.nock.core.model.execution;

import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.transition.TransitionImpl;
import io.machinecode.nock.core.inject.ResolvableReference;
import io.machinecode.nock.spi.element.execution.Decision;

import javax.batch.api.Decider;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class DecisionImpl extends ExecutionImpl implements Decision {

    private final List<TransitionImpl> transitions;
    private final PropertiesImpl properties;
    private final ResolvableReference<Decider> ref;

    public DecisionImpl(final String id, final String ref, final PropertiesImpl properties, final List<TransitionImpl> transitions) {
        super(id);
        this.transitions = transitions;
        this.properties = properties;
        this.ref = new ResolvableReference<Decider>(ref, Decider.class);
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
}
