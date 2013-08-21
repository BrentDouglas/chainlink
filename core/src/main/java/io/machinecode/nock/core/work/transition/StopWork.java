package io.machinecode.nock.core.work.transition;

import io.machinecode.nock.spi.element.transition.Stop;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class StopWork extends TransitionWork implements Stop {

    private final String restart;
    private final String exitStatus;

    public StopWork(final String on, final String restart, final String exitStatus) {
        super(on);
        this.restart = restart;
        this.exitStatus = exitStatus;
    }

    @Override
    public String getRestart() {
        return this.restart;
    }

    @Override
    public String getExitStatus() {
        return this.exitStatus;
    }
}
