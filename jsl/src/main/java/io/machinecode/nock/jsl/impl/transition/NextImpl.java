package io.machinecode.nock.jsl.impl.transition;

import io.machinecode.nock.jsl.api.transition.Next;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class NextImpl extends TransitionImpl implements Next {

    private final String to;

    public NextImpl(final Next that) {
        super(that);
        this.to = that.getTo();
    }

    @Override
    public String getTo() {
        return this.to;
    }
}
