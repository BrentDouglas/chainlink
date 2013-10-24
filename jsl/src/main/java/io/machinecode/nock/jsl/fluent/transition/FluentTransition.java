package io.machinecode.nock.jsl.fluent.transition;

import io.machinecode.nock.spi.Copyable;
import io.machinecode.nock.spi.element.transition.Transition;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class FluentTransition<T extends FluentTransition<T>> implements Copyable<T>, Transition {

    private String on;

    @Override
    public String getOn() {
        return this.on;
    }

    @SuppressWarnings("unchecked")
    public T setOn(final String on) {
        this.on = on;
        return (T)this;
    }
}
