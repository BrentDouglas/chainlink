package io.machinecode.nock.core.model.transition;

import io.machinecode.nock.spi.element.transition.Stop;
import org.jboss.logging.Logger;

import javax.batch.runtime.BatchStatus;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class StopImpl extends TransitionImpl implements Stop {

    private static final Logger log = Logger.getLogger(StopImpl.class);

    private final String exitStatus;
    private final String restart;

    public StopImpl(final String on, final String exitStatus, final String restart) {
        super(on);
        this.exitStatus = exitStatus;
        this.restart = restart;
    }

    @Override
    public String getExitStatus() {
        return this.exitStatus;
    }

    @Override
    public String getRestart() {
        return this.restart;
    }

    @Override
    public String element() {
        return Stop.ELEMENT;
    }

    @Override
    public BatchStatus getBatchStatus() {
        return BatchStatus.STOPPING;
    }

    @Override
    public String getNext() {
        return null;
    }

    @Override
    public String getRestartId() {
        return this.restart;
    }

    @Override
    public boolean isTerminating() {
        return true;
    }
}
