package io.machinecode.chainlink.core.element.transition;

import io.machinecode.chainlink.spi.element.transition.Transition;
import io.machinecode.chainlink.spi.work.TransitionWork;

import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public abstract class TransitionImpl implements Transition, TransitionWork, Serializable {
    private static final long serialVersionUID = 1L;

    private final String on;

    public TransitionImpl(final String on) {
        this.on = on;
    }

    @Override
    public String getOn() {
        return this.on;
    }
}
