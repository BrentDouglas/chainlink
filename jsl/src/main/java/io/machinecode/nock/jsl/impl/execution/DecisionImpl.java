package io.machinecode.nock.jsl.impl.execution;

import io.machinecode.nock.jsl.api.transition.Transition;
import io.machinecode.nock.jsl.api.execution.Decision;
import io.machinecode.nock.jsl.impl.PropertyReferenceImpl;
import io.machinecode.nock.jsl.impl.transition.TransitionImpl;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class DecisionImpl extends PropertyReferenceImpl implements Decision {

    private final String id;
    private final List<Transition> transitions;

    public DecisionImpl(final Decision that) {
        super(that);
        this.id = that.getId();
        this.transitions = TransitionImpl.immutableCopyTransitions(that.getTransitions());
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public List<Transition> getTransitions() {
        return this.transitions;
    }
}
