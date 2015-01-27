package io.machinecode.chainlink.core.jsl.impl.transition;

import io.machinecode.chainlink.spi.jsl.transition.Transition;

import javax.batch.runtime.BatchStatus;
import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public abstract class TransitionImpl implements Transition, Serializable {
    private static final long serialVersionUID = 1L;

    private final String on;

    public TransitionImpl(final String on) {
        this.on = on;
    }

    @Override
    public String getOn() {
        return this.on;
    }

    public abstract String getExitStatus();

    public abstract String element();

    public abstract BatchStatus getBatchStatus();

    public abstract String getNext();

    public abstract String getRestartId();

    public abstract boolean isTerminating();
}
