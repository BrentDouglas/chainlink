package io.machinecode.chainlink.core.jsl.impl.transition;

import io.machinecode.chainlink.spi.jsl.transition.Fail;
import org.jboss.logging.Logger;

import javax.batch.runtime.BatchStatus;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FailImpl extends TransitionImpl implements Fail {
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(FailImpl.class);

    private final String exitStatus;

    public FailImpl(final String on, final String exitStatus) {
        super(on);
        this.exitStatus = exitStatus;
    }

    @Override
    public String getExitStatus() {
        return this.exitStatus;
    }

    @Override
    public String element() {
        return Fail.ELEMENT;
    }

    @Override
    public BatchStatus getBatchStatus() {
        return BatchStatus.FAILED;
    }

    @Override
    public String getNext() {
        return null;
    }

    @Override
    public String getRestartId() {
        return null;
    }

    @Override
    public boolean isTerminating() {
        return true;
    }
}
