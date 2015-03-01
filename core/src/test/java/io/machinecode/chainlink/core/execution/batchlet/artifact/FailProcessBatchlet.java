package io.machinecode.chainlink.core.execution.batchlet.artifact;

import io.machinecode.chainlink.core.base.Reference;
import io.machinecode.chainlink.core.execution.batchlet.artifact.exception.FailProcessException;
import io.machinecode.chainlink.core.execution.batchlet.artifact.exception.FailStopException;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class FailProcessBatchlet extends javax.batch.api.AbstractBatchlet {

    public static final Reference<Boolean> hasStopped = new Reference<>(false);

    @Override
    public String process() throws Exception {
        synchronized (this) {
            while (!hasStopped.get()) {
                this.wait();
            }
        }
        throw new FailProcessException();
    }

    @Override
    public void stop() throws Exception {
        hasStopped.set(true);
        synchronized (this) {
            this.notifyAll();
        }
        throw new FailStopException();
    }

    public static void reset() {
        hasStopped.set(false);
    }
}
