package io.machinecode.chainlink.core.execution.artifact.partition;

import io.machinecode.chainlink.core.execution.artifact.exception.FailAnalyseException;

import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FailDataTestAnalyzer extends TestAnalyzer {

    @Override
    public void analyzeCollectorData(final Serializable data) throws Exception {
        super.analyzeCollectorData(data);
        throw new FailAnalyseException();
    }
}
