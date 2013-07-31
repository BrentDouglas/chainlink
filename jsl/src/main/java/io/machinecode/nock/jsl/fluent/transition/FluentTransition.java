package io.machinecode.nock.jsl.fluent.transition;

import io.machinecode.nock.jsl.api.transition.Transition;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class FluentTransition<T extends FluentTransition<T>> implements Transition {

    private String on;

    @Override
    public String getOn() {
        return this.on;
    }

    public T setOn(final String on) {
        this.on = on;
        return (T)this;
    }
}
