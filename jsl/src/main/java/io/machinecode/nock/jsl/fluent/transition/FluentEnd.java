package io.machinecode.nock.jsl.fluent.transition;

import io.machinecode.nock.jsl.api.transition.End;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentEnd extends FluentTransition<FluentEnd> implements End {

    private String exitStatus;

    @Override
    public String getExitStatus() {
        return this.exitStatus;
    }

    public FluentEnd setExitStatus(final String exitStatus) {
        this.exitStatus = exitStatus;
        return this;
    }
}
