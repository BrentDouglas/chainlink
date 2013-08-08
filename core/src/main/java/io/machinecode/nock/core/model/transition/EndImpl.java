package io.machinecode.nock.core.model.transition;

import io.machinecode.nock.jsl.api.transition.End;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class EndImpl extends TransitionImpl implements End {

    private final String exitStatus;

    public EndImpl(final String on, final String exitStatus) {
        super(on);
        this.exitStatus = exitStatus;
    }

    @Override
    public String getExitStatus() {
        return this.exitStatus;
    }
}
