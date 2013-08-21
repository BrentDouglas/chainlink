package io.machinecode.nock.core.work.transition;

import io.machinecode.nock.spi.element.transition.End;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class EndWork extends TransitionWork implements End {

    private final String exitStatus;

    public EndWork(final String on, final String exitStatus) {
        super(on);
        this.exitStatus = exitStatus;
    }

    @Override
    public String getExitStatus() {
        return this.exitStatus;
    }
}
