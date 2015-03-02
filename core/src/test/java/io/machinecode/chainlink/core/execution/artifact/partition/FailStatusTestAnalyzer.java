package io.machinecode.chainlink.core.execution.artifact.partition;

import io.machinecode.chainlink.core.execution.artifact.exception.FailAnalyseException;

import javax.batch.runtime.BatchStatus;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FailStatusTestAnalyzer extends TestAnalyzer {

    @Override
    public void analyzeStatus(final BatchStatus batchStatus, final String exitStatus) throws Exception {
        super.analyzeStatus(batchStatus, exitStatus);
        throw new FailAnalyseException();
    }
}
