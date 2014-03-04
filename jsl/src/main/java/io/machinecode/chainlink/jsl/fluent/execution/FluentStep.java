package io.machinecode.chainlink.jsl.fluent.execution;

import io.machinecode.chainlink.jsl.fluent.FluentInheritable;
import io.machinecode.chainlink.jsl.fluent.FluentListener;
import io.machinecode.chainlink.jsl.fluent.FluentListeners;
import io.machinecode.chainlink.jsl.fluent.FluentProperties;
import io.machinecode.chainlink.jsl.fluent.FluentProperty;
import io.machinecode.chainlink.jsl.fluent.partition.FluentPartition;
import io.machinecode.chainlink.jsl.fluent.partition.FluentStrategy;
import io.machinecode.chainlink.jsl.fluent.task.FluentTask;
import io.machinecode.chainlink.jsl.fluent.transition.FluentTransition;
import io.machinecode.chainlink.jsl.inherit.execution.InheritableStep;
import io.machinecode.chainlink.spi.Copyable;
import io.machinecode.chainlink.spi.JobRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentStep<T extends FluentTask, U extends Copyable<U> & FluentStrategy<U>>
        extends FluentInheritable<FluentStep<T, U>>
        implements FluentExecution<FluentStep<T, U>>, InheritableStep<FluentStep<T, U>, FluentProperties, FluentListeners, T, FluentTransition, FluentPartition<U>> {

    private String id;
    private String next;
    private String startLimit = ZERO;
    private String allowStartIfComplete = "false";
    private FluentProperties properties;
    private FluentListeners listeners;
    private List<FluentTransition> transitions = new ArrayList<FluentTransition>(0);
    private T task;
    private FluentPartition<U> partition;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public FluentStep<T, U> setId(final String id) {
        this.id = id;
        return this;
    }

    @Override
    public String getNext() {
        return this.next;
    }

    @Override
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
    public FluentProperties getProperties() {
        return this.properties;
    }

    @Override
    public FluentStep<T, U> setProperties(final FluentProperties properties) {
        this.properties = properties;
        return this;
    }

    public FluentStep<T, U> addProperty(final String name, final String value) {
        if (this.properties == null) {
            this.properties = new FluentProperties();
        }
        this.properties.getProperties().add(new FluentProperty().setName(name).setValue(value));
        return this;
    }

    @Override
    public FluentListeners getListeners() {
        return this.listeners;
    }

    @Override
    public FluentStep<T, U> setListeners(final FluentListeners listeners) {
        this.listeners = listeners;
        return this;
    }

    public FluentStep<T, U> addListener(final FluentListener listener) {
        if (this.listeners == null) {
            this.listeners = new FluentListeners();
        }
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
    public List<FluentTransition> getTransitions() {
        return this.transitions;
    }

    @Override
    public FluentStep<T, U> setTransitions(final List<FluentTransition> transitions) {
        this.transitions = transitions;
        return this;
    }

    public FluentStep<T, U> addTransition(final FluentTransition transition) {
        this.transitions.add(transition);
        return this;
    }

    @Override
    public FluentPartition<U> getPartition() {
        return this.partition;
    }

    @Override
    public FluentStep<T, U> setPartition(final FluentPartition<U> partition) {
        this.partition = partition;
        return this;
    }

    @Override
    public FluentStep<T, U> inherit(final JobRepository repository, final String defaultJobXml) {
        return StepTool.inherit(FluentStep.class, this, repository, defaultJobXml);
    }

    @Override
    public FluentStep<T, U> copy() {
        return copy(new FluentStep<T, U>());
    }

    @Override
    public FluentStep<T, U> copy(final FluentStep<T, U> that) {
        return StepTool.copy(this, that);
    }
}
