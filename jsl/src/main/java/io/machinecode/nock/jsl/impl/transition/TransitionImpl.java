package io.machinecode.nock.jsl.impl.transition;

import io.machinecode.nock.jsl.api.transition.Transition;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class TransitionImpl implements Transition {

    private final String on;

    public TransitionImpl(final Transition that) {
        this.on = that.getOn();
    }

    @Override
    public String getOn() {
        return this.on;
    }

}
