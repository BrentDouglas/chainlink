package io.machinecode.chainlink.test.core.execution.artifact.batchlet;

import io.machinecode.chainlink.test.core.execution.Reference;

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
