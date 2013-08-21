package io.machinecode.nock.core.work.transition;

import io.machinecode.nock.spi.element.transition.Next;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class NextWork extends TransitionWork implements Next {

    private final String to;

    public NextWork(final String on, final String to) {
        super(on);
        this.to = to;
    }

    @Override
    public String getTo() {
        return this.to;
    }
}
