package io.machinecode.nock.jsl.fluent.type;

import io.machinecode.nock.jsl.api.transition.Transition;
import io.machinecode.nock.jsl.api.type.Flow;
import io.machinecode.nock.jsl.api.type.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentFlow extends FluentType<FluentFlow> implements Flow {

    private String next;
    private final List<Type> types = new ArrayList<Type>(0);
    private final List<Transition> transitions = new ArrayList<Transition>(0);

    @Override
    public String getNext() {
        return this.next;
    }

    public FluentFlow setNext(final String next) {
        this.next = next;
        return this;
    }

    @Override
    public List<Type> getTypes() {
        return this.types;
    }

    public FluentFlow addType(final Type type) {
        this.types.add(type);
        return this;
    }

    @Override
    public List<Transition> getTransitions() {
        return this.transitions;
    }

    public FluentFlow addTransition(final Transition transition) {
        this.transitions.add(transition);
        return this;
    }
}
