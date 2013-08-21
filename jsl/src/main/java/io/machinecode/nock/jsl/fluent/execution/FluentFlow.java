package io.machinecode.nock.jsl.fluent.execution;

import io.machinecode.nock.spi.element.execution.Execution;
import io.machinecode.nock.spi.element.transition.Transition;
import io.machinecode.nock.spi.element.execution.Flow;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentFlow extends FluentExecution<FluentFlow> implements Flow {

    private String next;
    private final List<Execution> executions = new ArrayList<Execution>(0);
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
    public List<Execution> getExecutions() {
        return this.executions;
    }

    public FluentFlow addExecution(final Execution execution) {
        this.executions.add(execution);
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
