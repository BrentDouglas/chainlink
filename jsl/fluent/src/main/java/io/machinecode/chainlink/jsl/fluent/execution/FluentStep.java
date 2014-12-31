package io.machinecode.chainlink.jsl.fluent.execution;

import io.machinecode.chainlink.jsl.fluent.FluentInheritable;
import io.machinecode.chainlink.jsl.fluent.FluentListener;
import io.machinecode.chainlink.jsl.fluent.FluentListeners;
import io.machinecode.chainlink.jsl.fluent.FluentProperties;
import io.machinecode.chainlink.jsl.fluent.FluentProperty;
import io.machinecode.chainlink.jsl.fluent.partition.FluentPartition;
import io.machinecode.chainlink.jsl.fluent.task.FluentBatchlet;
import io.machinecode.chainlink.jsl.fluent.task.FluentChunk;
import io.machinecode.chainlink.jsl.fluent.task.FluentTask;
import io.machinecode.chainlink.jsl.fluent.transition.FluentTransition;
import io.machinecode.chainlink.spi.jsl.inherit.execution.InheritableStep;
import io.machinecode.chainlink.spi.loader.JobRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FluentStep
        extends FluentInheritable<FluentStep>
        implements FluentExecution<FluentStep>, InheritableStep<FluentStep, FluentProperties, FluentListeners, FluentTask, FluentTransition, FluentPartition> {

    private String id;
    private String next;
    private String startLimit = ZERO;
    private String allowStartIfComplete = "false";
    private FluentProperties properties;
    private FluentListeners listeners;
    private List<FluentTransition> transitions = new ArrayList<>(0);
    private FluentTask task;
    private FluentPartition partition;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public FluentStep setId(final String id) {
        this.id = id;
        return this;
    }

    @Override
    public String getNext() {
        return this.next;
    }

    @Override
    public FluentStep setNext(final String next) {
        this.next = next;
        return this;
    }

    @Override
    public String getStartLimit() {
        return this.startLimit;
    }

    public FluentStep setStartLimit(final String startLimit) {
        this.startLimit = startLimit;
        return this;
    }

    @Override
    public String getAllowStartIfComplete() {
        return this.allowStartIfComplete;
    }

    public FluentStep setAllowStartIfComplete(final String allowStartIfComplete) {
        this.allowStartIfComplete = allowStartIfComplete;
        return this;
    }

    @Override
    public FluentProperties getProperties() {
        return this.properties;
    }

    @Override
    public FluentStep setProperties(final FluentProperties properties) {
        this.properties = properties;
        return this;
    }

    public FluentStep addProperty(final String name, final String value) {
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
    public FluentStep setListeners(final FluentListeners listeners) {
        this.listeners = listeners;
        return this;
    }

    public FluentStep addListener(final FluentListener listener) {
        if (this.listeners == null) {
            this.listeners = new FluentListeners();
        }
        this.listeners.addListener(listener);
        return this;
    }

    @Override
    public FluentTask getTask() {
        return this.task;
    }

    public FluentStep setTask(final FluentTask task) {
        this.task = task;
        return this;
    }

    public FluentStep setBatchlet(final FluentBatchlet task) {
        this.task = task;
        return this;
    }

    public FluentStep setChunk(final FluentChunk task) {
        this.task = task;
        return this;
    }

    @Override
    public List<FluentTransition> getTransitions() {
        return this.transitions;
    }

    @Override
    public FluentStep setTransitions(final List<FluentTransition> transitions) {
        this.transitions = transitions;
        return this;
    }

    public FluentStep addTransition(final FluentTransition transition) {
        this.transitions.add(transition);
        return this;
    }

    @Override
    public FluentPartition getPartition() {
        return this.partition;
    }

    @Override
    public FluentStep setPartition(final FluentPartition partition) {
        this.partition = partition;
        return this;
    }

    @Override
    public FluentStep inherit(final JobRepository repository, final String defaultJobXml) {
        return StepTool.inherit(FluentStep.class, this, repository, defaultJobXml);
    }

    @Override
    public FluentStep copy() {
        return copy(new FluentStep());
    }

    @Override
    public FluentStep copy(final FluentStep that) {
        return StepTool.copy(this, that);
    }
}
