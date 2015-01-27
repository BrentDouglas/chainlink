package io.machinecode.chainlink.core.jsl.fluent.transition;

import io.machinecode.chainlink.spi.jsl.inherit.Copyable;
import io.machinecode.chainlink.spi.jsl.transition.Transition;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
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
