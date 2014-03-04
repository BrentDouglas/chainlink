package io.machinecode.chainlink.core.model.transition;

import io.machinecode.chainlink.spi.element.transition.Next;
import org.jboss.logging.Logger;

import javax.batch.runtime.BatchStatus;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class NextImpl extends TransitionImpl implements Next {

    private static final Logger log = Logger.getLogger(NextImpl.class);

    private final String to;

    public NextImpl(final String on, final String to) {
        super(on);
        this.to = to;
    }

    @Override
    public String getTo() {
        return this.to;
    }

    @Override
    public String element() {
        return Next.ELEMENT;
    }

    @Override
    public String getExitStatus() {
        return null;
    }

    @Override
    public BatchStatus getBatchStatus() {
        return null;
    }

    @Override
    public String getNext() {
        return this.to;
    }

    @Override
    public String getRestartId() {
        return null;
    }

    @Override
    public boolean isTerminating() {
        return false;
    }
}
