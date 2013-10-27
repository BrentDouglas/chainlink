package io.machinecode.nock.core.model.transition;

import io.machinecode.nock.spi.element.transition.End;
import org.jboss.logging.Logger;

import javax.batch.runtime.BatchStatus;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class EndImpl extends TransitionImpl implements End {

    private static final Logger log = Logger.getLogger(EndImpl.class);

    private final String exitStatus;

    public EndImpl(final String on, final String exitStatus) {
        super(on);
        this.exitStatus = exitStatus;
    }

    @Override
    public String getExitStatus() {
        return this.exitStatus;
    }

    @Override
    public Result runTransition() throws Exception {
        return Result.status(BatchStatus.COMPLETED, this.exitStatus);
    }
}
