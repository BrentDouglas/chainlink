package io.machinecode.nock.jsl.fluent.execution;

import io.machinecode.nock.jsl.fluent.FluentProperties;
import io.machinecode.nock.jsl.fluent.FluentPropertyReference;
import io.machinecode.nock.jsl.fluent.transition.FluentTransition;
import io.machinecode.nock.jsl.inherit.execution.InheritableDecision;
import io.machinecode.nock.spi.JobRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentDecision extends FluentPropertyReference<FluentDecision> implements FluentExecution<FluentDecision>, InheritableDecision<FluentDecision, FluentProperties, FluentTransition> {

    private String id;
    private List<FluentTransition> transitions = new ArrayList<FluentTransition>(0);

    @Override
    public String getId() {
        return this.id;
    }

    public FluentDecision setId(final String id) {
        this.id = id;
        return this;
    }

    @Override
    public List<FluentTransition> getTransitions() {
        return this.transitions;
    }

    @Override
    public FluentDecision setTransitions(final List<FluentTransition> transitions) {
        this.transitions = transitions;
        return this;
    }

    public FluentDecision addTransition(final FluentTransition transition) {
        this.transitions.add(transition);
        return this;
    }

    @Override
    public FluentDecision inherit(final JobRepository repository, final String defaultJobXml) {
        return DecisionTool.inherit(this, repository, defaultJobXml);
    }

    @Override
    public FluentDecision copy() {
        return copy(new FluentDecision());
    }
}
