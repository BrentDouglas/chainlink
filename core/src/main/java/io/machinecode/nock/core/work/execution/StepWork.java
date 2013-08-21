package io.machinecode.nock.core.work.execution;

import io.machinecode.nock.core.work.ListenersWork;
import io.machinecode.nock.core.work.Work;
import io.machinecode.nock.core.work.partition.PartitionWork;
import io.machinecode.nock.core.work.transition.TransitionWork;
import io.machinecode.nock.spi.element.Properties;
import io.machinecode.nock.spi.element.execution.Step;
import io.machinecode.nock.spi.element.partition.Strategy;
import io.machinecode.nock.spi.element.task.Task;

import javax.batch.api.listener.StepListener;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class StepWork<T extends Task & Work, U extends Strategy & Work, V extends PartitionWork<U>> extends ExecutionWork implements Step<T, U> {

    private final String next;
    private final String startLimit;
    private final String allowStartIfComplete;
    private final ListenersWork<StepListener> listeners;
    private final List<TransitionWork> transitions;
    private final T task;
    private final V partition;

    public StepWork(final String id,
                    final String next,
                    final String startLimit,
                    final String allowStartIfComplete,
                    final ListenersWork<StepListener> listeners,
                    final List<TransitionWork> transitions,
                    final T task,
                    final V partition
    ) {
        super(id);
        this.next = next;
        this.startLimit = startLimit;
        this.allowStartIfComplete = allowStartIfComplete;
        this.listeners = listeners;
        this.transitions = transitions;
        this.task = task;
        this.partition = partition;
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
        return null;
    }

    @Override
    public ListenersWork<StepListener> getListeners() {
        return this.listeners;
    }

    @Override
    public T getTask() {
        return this.task;
    }

    @Override
    public List<TransitionWork> getTransitions() {
        return this.transitions;
    }

    @Override
    public V getPartition() {
        return this.partition;
    }

    @Override
    public String getNext() {
        return this.next;
    }
}
