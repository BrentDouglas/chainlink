package io.machinecode.nock.core.work.transition;

import io.machinecode.nock.spi.element.transition.Fail;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FailWork extends TransitionWork implements Fail {

    private final String exitStatus;

    public FailWork(final String on, final String exitStatus) {
        super(on);
        this.exitStatus = exitStatus;
    }

    @Override
    public String getExitStatus() {
        return this.exitStatus;
    }
}
