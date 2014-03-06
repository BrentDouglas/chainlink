package io.machinecode.chainlink.jsl.fluent.transition;

import io.machinecode.chainlink.jsl.core.inherit.transition.InheritableStop;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentStop extends FluentTerminatingTransition<FluentStop> implements InheritableStop<FluentStop> {

    private String restart;

    @Override
    public String getRestart() {
        return this.restart;
    }

    public FluentStop setRestart(final String restart) {
        this.restart = restart;
        return this;
    }

    @Override
    public FluentStop copy() {
        return copy(new FluentStop());
    }

    @Override
    public FluentStop copy(final FluentStop that) {
        return StopTool.copy(this, that);
    }
}
