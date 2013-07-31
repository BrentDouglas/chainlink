package io.machinecode.nock.jsl.impl.transition;

import io.machinecode.nock.jsl.api.transition.End;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class EndImpl extends TransitionImpl implements End {

    private final String exitStatus;

    public EndImpl(final End that) {
        super(that);
        this.exitStatus = that.getExitStatus();
    }

    @Override
    public String getExitStatus() {
        return this.exitStatus;
    }
}
