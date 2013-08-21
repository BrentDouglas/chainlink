package io.machinecode.nock.core.descriptor.transition;

import io.machinecode.nock.spi.element.transition.Transition;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class TransitionImpl implements Transition {

    private final String on;

    public TransitionImpl(final String on) {
        this.on = on;
    }

    @Override
    public String getOn() {
        return this.on;
    }
}
