package io.machinecode.chainlink.jsl.fluent.transition;

import io.machinecode.chainlink.jsl.core.inherit.transition.InheritableTerminatingTransition;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class FluentTerminatingTransition<T extends FluentTerminatingTransition<T>> extends FluentTransition<T> implements InheritableTerminatingTransition<T> {

    private String exitStatus;

    @Override
    public String getExitStatus() {
        return exitStatus;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setExitStatus(final String exitStatus) {
        this.exitStatus = exitStatus;
        return (T)this;
    }

    @Override
    public T copy(final T that) {
        return TerminatingTransitionTool.copy((T)this, that);
    }
}
