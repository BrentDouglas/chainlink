package io.machinecode.chainlink.core.model.transition;

import io.machinecode.chainlink.spi.element.transition.Transition;
import io.machinecode.chainlink.spi.work.TransitionWork;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class TransitionImpl implements Transition, TransitionWork {

    private final String on;

    public TransitionImpl(final String on) {
        this.on = on;
    }

    @Override
    public String getOn() {
        return this.on;
    }
}
