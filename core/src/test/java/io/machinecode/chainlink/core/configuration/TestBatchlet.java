package io.machinecode.chainlink.core.configuration;

import javax.batch.api.Batchlet;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class TestBatchlet implements Batchlet {
    @Override
    public String process() throws Exception {
        return null;
    }

    @Override
    public void stop() throws Exception {
      
    }
}
