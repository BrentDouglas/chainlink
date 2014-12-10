package io.machinecode.chainlink.jsl.fluent.transition;

import io.machinecode.chainlink.jsl.core.inherit.transition.InheritableNext;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FluentNext extends FluentTransition<FluentNext> implements InheritableNext<FluentNext> {

    private String to;

    @Override
    public String getTo() {
        return this.to;
    }

    public FluentNext setTo(final String to) {
        this.to = to;
        return this;
    }

    @Override
    public FluentNext copy() {
        return copy(new FluentNext());
    }

    @Override
    public FluentNext copy(final FluentNext that) {
        return NextTool.copy(this, that);
    }
}
