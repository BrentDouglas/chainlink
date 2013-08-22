package io.machinecode.nock.core.model.execution;

import io.machinecode.nock.core.model.transition.TransitionImpl;
import io.machinecode.nock.spi.element.execution.Flow;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FlowImpl extends ExecutionImpl implements Flow {

    private final String next;
    private final List<ExecutionImpl> executions;
    private final List<TransitionImpl> transitions;

    public FlowImpl(final String id, final String next, final List<ExecutionImpl> executions, final List<TransitionImpl> transitions) {
        super(id);
        this.next = next;
        this.executions = executions;
        this.transitions = transitions;
    }

    @Override
    public String getNext() {
        return this.next;
    }

    @Override
    public List<ExecutionImpl> getExecutions() {
        return this.executions;
    }

    @Override
    public List<TransitionImpl> getTransitions() {
        return this.transitions;
    }
}
