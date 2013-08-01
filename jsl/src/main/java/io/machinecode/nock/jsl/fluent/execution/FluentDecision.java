package io.machinecode.nock.jsl.fluent.execution;

import io.machinecode.nock.jsl.api.transition.Transition;
import io.machinecode.nock.jsl.api.execution.Decision;
import io.machinecode.nock.jsl.fluent.FluentPropertyReference;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentDecision extends FluentPropertyReference<FluentDecision> implements Decision {

    private String id;
    private final List<Transition> transitions = new ArrayList<Transition>(0);

    @Override
    public String getId() {
        return this.id;
    }

    public FluentDecision setId(final String id) {
        this.id = id;
        return this;
    }

    @Override
    public List<Transition> getTransitions() {
        return this.transitions;
    }

    public FluentDecision addTransition(final Transition transition) {
        this.transitions.add(transition);
        return this;
    }
}
