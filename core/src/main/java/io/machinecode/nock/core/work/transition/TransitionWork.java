package io.machinecode.nock.core.work.transition;

import io.machinecode.nock.core.work.Work;
import io.machinecode.nock.spi.element.transition.Transition;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class TransitionWork implements Work, Transition {

    private final String on;

    protected TransitionWork(final String on) {
        this.on = on;
    }

    @Override
    public String getOn() {
        return this.on;
    }
}
