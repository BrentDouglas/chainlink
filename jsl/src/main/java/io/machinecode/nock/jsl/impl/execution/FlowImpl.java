package io.machinecode.nock.jsl.impl.execution;

import io.machinecode.nock.jsl.api.execution.Execution;
import io.machinecode.nock.jsl.api.execution.Flow;
import io.machinecode.nock.jsl.api.transition.Transition;
import io.machinecode.nock.jsl.impl.transition.TransitionImpl;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FlowImpl extends ExecutionImpl implements Flow {

    private final String next;
    private final List<Execution> executions;
    private final List<Transition> transitions;

    public FlowImpl(final Flow that, final Execution execution) {
        super(that);
        this.next = that.getNext() == null ? execution == null ? null : execution.getId() : that.getNext();
        this.executions = ExecutionImpl.immutableCopyExecutions(that.getExecutions());
        this.transitions = TransitionImpl.immutableCopyTransitions(that.getTransitions());
    }

    @Override
    public String getNext() {
        return this.next;
    }

    @Override
    public List<Execution> getExecutions() {
        return this.executions;
    }

    @Override
    public List<Transition> getTransitions() {
        return this.transitions;
    }
}
