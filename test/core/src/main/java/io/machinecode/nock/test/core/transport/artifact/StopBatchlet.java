package io.machinecode.nock.test.core.transport.artifact;

import io.machinecode.nock.jsl.util.Reference;

import javax.batch.runtime.BatchStatus;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class StopBatchlet extends javax.batch.api.AbstractBatchlet {
    public static Reference<Boolean> hasRun = new Reference<Boolean>(false);
    public static Reference<Boolean> hasStopped = new Reference<Boolean>(false);
    @Override
    public String process() throws Exception {
        hasRun.set(true);
        try {
            synchronized (this) {
                wait();
            }
        } catch (final InterruptedException e) {
            //
        }
        return BatchStatus.COMPLETED.toString();
    }

    @Override
    public void stop() throws Exception {
        hasStopped.set(true);
        synchronized (this) {
            notifyAll();
        }
    }
}
