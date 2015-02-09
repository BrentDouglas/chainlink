package io.machinecode.chainlink.core.jsl.fluent.execution;

import io.machinecode.chainlink.core.jsl.fluent.FluentProperties;
import io.machinecode.chainlink.core.jsl.fluent.FluentPropertyReference;
import io.machinecode.chainlink.core.jsl.fluent.transition.FluentTransition;
import io.machinecode.chainlink.spi.jsl.inherit.execution.InheritableDecision;
import io.machinecode.chainlink.spi.loader.InheritableJobLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FluentDecision extends FluentPropertyReference<FluentDecision> implements FluentExecution<FluentDecision>, InheritableDecision<FluentDecision, FluentProperties, FluentTransition> {

    private String id;
    private List<FluentTransition> transitions = new ArrayList<>(0);

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
    public FluentDecision inherit(final InheritableJobLoader repository, final String defaultJobXml) {
        return DecisionTool.inherit(this, repository, defaultJobXml);
    }

    @Override
    public FluentDecision copy() {
        return copy(new FluentDecision());
    }
}
