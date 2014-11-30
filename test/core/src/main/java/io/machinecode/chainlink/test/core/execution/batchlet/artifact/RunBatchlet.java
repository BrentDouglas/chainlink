package io.machinecode.chainlink.test.core.execution.batchlet.artifact;

import io.machinecode.chainlink.test.core.execution.Reference;

import javax.batch.runtime.BatchStatus;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class RunBatchlet extends javax.batch.api.AbstractBatchlet {
    public static Reference<Boolean> hasRun = new Reference<Boolean>(false);
    @Override
    public String process() throws Exception {
        hasRun.set(true);
        return BatchStatus.COMPLETED.toString();
    }
}
