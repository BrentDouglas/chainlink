package io.machinecode.nock.jsl.fluent.transition;

import io.machinecode.nock.jsl.inherit.transition.InheritableNext;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
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
