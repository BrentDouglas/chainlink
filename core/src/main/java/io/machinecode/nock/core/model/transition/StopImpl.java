package io.machinecode.nock.core.model.transition;

import io.machinecode.nock.spi.element.transition.Stop;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class StopImpl extends TransitionImpl implements Stop {

    private final String exitStatus;
    private final String restart;

    public StopImpl(final String on, final String exitStatus, final String restart) {
        super(on);
        this.exitStatus = exitStatus;
        this.restart = restart;
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
