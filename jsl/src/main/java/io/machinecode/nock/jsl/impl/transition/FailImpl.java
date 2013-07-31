package io.machinecode.nock.jsl.impl.transition;

import io.machinecode.nock.jsl.api.transition.Fail;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FailImpl extends TransitionImpl implements Fail {

    private final String exitStatus;

    public FailImpl(final Fail that) {
        super(that);
        this.exitStatus = that.getExitStatus();
    }

    @Override
    public String getExitStatus() {
        return this.exitStatus;
    }
}
