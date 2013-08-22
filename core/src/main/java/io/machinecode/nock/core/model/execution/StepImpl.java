package io.machinecode.nock.core.model.execution;

import io.machinecode.nock.core.model.ListenersImpl;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.partition.PartitionImpl;
import io.machinecode.nock.core.model.transition.TransitionImpl;
import io.machinecode.nock.spi.element.execution.Step;
import io.machinecode.nock.spi.element.partition.Strategy;
import io.machinecode.nock.spi.element.task.Task;

import javax.batch.api.listener.StepListener;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class StepImpl<T extends Task, U extends Strategy> extends ExecutionImpl implements Step<T, U> {

    private final String next;
    private final String startLimit;
    private final String allowStartIfComplete;
    private final PropertiesImpl properties;
    private final ListenersImpl<StepListener> listeners;
    private final List<TransitionImpl> transitions;
    private final T task;
    private final PartitionImpl<U> partition;

    public StepImpl(
            final String id,
            final String next,
            final String startLimit,
            final String allowStartIfComplete,
            final PropertiesImpl properties,
            final ListenersImpl<StepListener> listeners,
            final List<TransitionImpl> transitions,
            final T task,
            final PartitionImpl<U> partition
    ) {
        super(id);
        this.next = next;
        this.startLimit = startLimit;
        this.allowStartIfComplete = allowStartIfComplete;
        this.listeners = listeners;
        this.properties = properties;
        this.transitions = transitions;
        this.task = task;
        this.partition = partition;
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
    public PropertiesImpl getProperties() {
        return this.properties;
    }

    @Override
    public ListenersImpl<StepListener> getListeners() {
        return this.listeners;
    }

    @Override
    public List<TransitionImpl> getTransitions() {
        return this.transitions;
    }

    @Override
    public T getTask() {
        return this.task;
    }

    @Override
    public PartitionImpl<U> getPartition() {
        return this.partition;
    }
}
