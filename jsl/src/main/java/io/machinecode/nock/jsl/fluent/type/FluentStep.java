package io.machinecode.nock.jsl.fluent.type;

import io.machinecode.nock.jsl.api.Listener;
import io.machinecode.nock.jsl.api.Listeners;
import io.machinecode.nock.jsl.api.Part;
import io.machinecode.nock.jsl.api.Properties;
import io.machinecode.nock.jsl.api.partition.Mapper;
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
public abstract class FluentStep<T extends Part, U extends Mapper> extends FluentExecution<FluentStep<T, U>> implements Step <T, U> {

    private String next;
    private int startLimit = 0;
    private boolean allowStartIfComplete = false;
    private final FluentListeners listeners = new FluentListeners();
    private final FluentProperties properties = new FluentProperties();
    private final List<Transition> transitions = new ArrayList<Transition>(0);
    private T part;
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
    public int getStartLimit() {
        return this.startLimit;
    }

    public FluentStep<T, U> setStartLimit(final int startLimit) {
        this.startLimit = startLimit;
        return this;
    }

    @Override
    public boolean isAllowStartIfComplete() {
        return this.allowStartIfComplete;
    }

    public FluentStep<T, U> setAllowStartIfComplete(final boolean allowStartIfComplete) {
        this.allowStartIfComplete = allowStartIfComplete;
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
    public Properties getProperties() {
        return this.properties;
    }

    public FluentStep<T, U> addProperty(final String name, final String value) {
        this.properties.getProperties().add(new FluentProperty().setName(name).setValue(value));
        return this;
    }

    @Override
    public T getPart() {
        return this.part;
    }

    public FluentStep<T, U> setPart(final T part) {
        this.part = part;
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
