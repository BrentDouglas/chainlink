package io.machinecode.nock.jsl.fluent.transition;

import io.machinecode.nock.jsl.api.transition.Fail;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentFail extends FluentTransition<FluentFail> implements Fail {

    private String exitStatus;

    @Override
    public String getExitStatus() {
        return this.exitStatus;
    }

    public FluentFail setExitStatus(final String exitStatus) {
        this.exitStatus = exitStatus;
        return this;
    }
}
