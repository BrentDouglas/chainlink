package io.machinecode.nock.core.descriptor.transition;

import io.machinecode.nock.spi.element.transition.Next;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class NextImpl extends TransitionImpl implements Next {

    private final String to;

    public NextImpl(final String on, final String to) {
        super(on);
        this.to = to;
    }

    @Override
    public String getTo() {
        return this.to;
    }
}
