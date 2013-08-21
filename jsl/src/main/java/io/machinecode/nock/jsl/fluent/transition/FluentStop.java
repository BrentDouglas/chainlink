package io.machinecode.nock.jsl.fluent.transition;

import io.machinecode.nock.spi.element.transition.Stop;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentStop extends FluentTransition<FluentStop> implements Stop {

    private String exitStatus;
    private String restart;

    @Override
    public String getExitStatus() {
        return this.exitStatus;
    }

    public FluentStop setExitStatus(final String exitStatus) {
        this.exitStatus = exitStatus;
        return this;
    }

    @Override
    public String getRestart() {
        return this.restart;
    }

    public FluentStop setRestart(final String restart) {
        this.restart = restart;
        return this;
    }
}
