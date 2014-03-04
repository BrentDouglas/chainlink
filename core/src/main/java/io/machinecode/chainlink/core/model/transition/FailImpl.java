package io.machinecode.chainlink.core.model.transition;

import io.machinecode.chainlink.spi.element.transition.Fail;
import org.jboss.logging.Logger;

import javax.batch.runtime.BatchStatus;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FailImpl extends TransitionImpl implements Fail {

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
