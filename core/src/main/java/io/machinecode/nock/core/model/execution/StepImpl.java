package io.machinecode.nock.core.model.execution;

import io.machinecode.nock.core.model.transition.TransitionImpl;
import io.machinecode.nock.jsl.api.Listeners;
import io.machinecode.nock.jsl.api.Properties;
import io.machinecode.nock.jsl.api.execution.Step;
import io.machinecode.nock.jsl.api.partition.Strategy;
import io.machinecode.nock.jsl.api.task.Task;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class StepImpl<T extends Task, U extends Strategy> extends ExecutionImpl implements Step<T, U> {

    private final String next;
    private final String startLimit;
    private final String allowStartIfComplete;
    private final Properties properties;
    private final Listeners listeners;
    private final List<TransitionImpl> transitions;

    public StepImpl(
            final String id,
            final String next,
            final String startLimit,
            final String allowStartIfComplete,
            final Properties properties,
            final Listeners listeners,
            final List<TransitionImpl> transitions
    ) {
        super(id);
        this.next = next;
        this.startLimit = startLimit;
        this.allowStartIfComplete = allowStartIfComplete;
        this.listeners = listeners;
        this.properties = properties;
        this.transitions = transitions;
    }

    @Override
    public String getNext() {
        return this.next;
    }

    @Override
    public String getStartLimit() {
        return this.startLimit;
    }

    @Override
    public String getAllowStartIfComplete() {
        return this.allowStartIfComplete;
    }

    @Override
    public Properties getProperties() {
        return this.properties;
    }

    @Override
    public Listeners getListeners() {
        return this.listeners;
    }

    @Override
    public List<TransitionImpl> getTransitions() {
        return this.transitions;
    }
}
