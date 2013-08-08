package io.machinecode.nock.jsl.fluent.execution;

import io.machinecode.nock.jsl.api.Listener;
import io.machinecode.nock.jsl.api.Listeners;
import io.machinecode.nock.jsl.api.task.Task;
import io.machinecode.nock.jsl.api.Properties;
import io.machinecode.nock.jsl.api.partition.Strategy;
import io.machinecode.nock.jsl.api.partition.Partition;
import io.machinecode.nock.jsl.api.transition.Transition;
import io.machinecode.nock.jsl.api.execution.Step;
import io.machinecode.nock.jsl.fluent.FluentListeners;
import io.machinecode.nock.jsl.fluent.FluentProperties;
import io.machinecode.nock.jsl.fluent.FluentProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class FluentStep<T extends Task, U extends Strategy> extends FluentExecution<FluentStep<T, U>> implements Step <T, U> {

    private String next;
    private String startLimit = ZERO;
    private String allowStartIfComplete = "false";
    private final FluentProperties properties = new FluentProperties();
    private final FluentListeners listeners = new FluentListeners();
    private final List<Transition> transitions = new ArrayList<Transition>(0);
    private T task;
    private Partition<U> partition;

    @Override
    public String getNext() {
        return this.next;
    }

    public FluentStep<T, U> setNext(final String next) {
        this.next = next;
        return this;
    }

    @Override
    public String getStartLimit() {
        return this.startLimit;
    }

    public FluentStep<T, U> setStartLimit(final String startLimit) {
        this.startLimit = startLimit;
        return this;
    }

    @Override
    public String getAllowStartIfComplete() {
        return this.allowStartIfComplete;
    }

    public FluentStep<T, U> setAllowStartIfComplete(final String allowStartIfComplete) {
        this.allowStartIfComplete = allowStartIfComplete;
        return this;
    }

    @Override
    public Properties getProperties() {
        return this.properties;
    }

    public FluentStep<T, U> addProperty(final String name, final String value) {
        this.properties.getProperties().add(new FluentProperty().setName(name).setValue(value));
        return this;
    }

    @Override
    public Listeners getListeners() {
        return this.listeners;
    }

    public FluentStep<T, U> addListener(final Listener listener) {
        this.listeners.addListener(listener);
        return this;
    }

    @Override
    public T getTask() {
        return this.task;
    }

    public FluentStep<T, U> setTask(final T task) {
        this.task = task;
        return this;
    }

    @Override
    public List<Transition> getTransitions() {
        return this.transitions;
    }

    public FluentStep<T, U> addTransition(final Transition transition) {
        this.transitions.add(transition);
        return this;
    }

    @Override
    public Partition<U> getPartition() {
        return this.partition;
    }

    public FluentStep<T, U> setPartition(final Partition<U> partition) {
        this.partition = partition;
        return this;
    }
}
