package io.machinecode.nock.jsl.impl.transition;

import io.machinecode.nock.jsl.api.transition.Stop;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class StopImpl extends TransitionImpl implements Stop {

    private final String exitStatus;
    private final String restart;

    public StopImpl(final Stop that) {
        super(that);
        this.exitStatus = that.getExitStatus();
        this.restart = that.getRestart();
    }

    @Override
    public String getExitStatus() {
        return this.exitStatus;
    }

    @Override
    public String getRestart() {
        return this.restart;
    }
}
