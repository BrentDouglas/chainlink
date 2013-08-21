package io.machinecode.nock.core.work.execution;

import io.machinecode.nock.core.work.transition.TransitionWork;
import io.machinecode.nock.spi.element.execution.Flow;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FlowWork extends ExecutionWork implements Flow {

    private final String next;
    private final List<ExecutionWork> executions;
    private final List<TransitionWork> transitions;

    public FlowWork(final String id, final String next, final List<ExecutionWork> executions, final List<TransitionWork> transitions) {
        super(id);
        this.next = next;
        this.executions = executions;
        this.transitions = transitions;
    }

    @Override
    public List<ExecutionWork> getExecutions() {
        return this.executions;
    }

    @Override
    public List<TransitionWork> getTransitions() {
        return this.transitions;
    }

    @Override
    public String getNext() {
        return this.next;
    }
}
