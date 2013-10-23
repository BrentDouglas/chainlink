package io.machinecode.nock.test.core.transport.artifact.batchlet;

import io.machinecode.nock.jsl.util.Reference;

import javax.batch.runtime.BatchStatus;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class RunBatchlet extends javax.batch.api.AbstractBatchlet {
    public static Reference<Boolean> hasRun = new Reference<Boolean>(false);
    @Override
    public String process() throws Exception {
        hasRun.set(true);
        return BatchStatus.COMPLETED.toString();
    }
}
