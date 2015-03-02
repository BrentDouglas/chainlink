package io.machinecode.chainlink.core.execution.artifact.batchlet;

import io.machinecode.chainlink.core.base.Reference;

import javax.batch.runtime.BatchStatus;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class StopBatchlet extends javax.batch.api.AbstractBatchlet {

    public static final Reference<Boolean> hasStopped = new Reference<>(false);

    @Override
    public String process() throws Exception {
        synchronized (this) {
            while (!hasStopped.get()) {
                this.wait();
            }
        }
        return BatchStatus.COMPLETED.toString();
    }

    @Override
    public void stop() throws Exception {
        hasStopped.set(true);
        synchronized (this) {
            this.notifyAll();
        }
    }

    public static void reset() {
        hasStopped.set(false);
    }
}
