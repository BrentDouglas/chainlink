package io.machinecode.chainlink.core.execution.batchlet.artifact;

import io.machinecode.chainlink.core.base.Reference;
import io.machinecode.chainlink.core.execution.batchlet.artifact.exception.FailProcessException;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class FailBatchlet extends javax.batch.api.AbstractBatchlet {

    public static final Reference<Boolean> hasRun = new Reference<>(false);

    @Override
    public String process() throws Exception {
        hasRun.set(true);
        throw new FailProcessException();
    }
}
