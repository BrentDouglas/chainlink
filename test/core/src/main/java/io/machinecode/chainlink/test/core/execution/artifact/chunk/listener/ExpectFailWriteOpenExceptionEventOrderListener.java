package io.machinecode.chainlink.test.core.execution.artifact.chunk.listener;

import io.machinecode.chainlink.test.core.execution.artifact.chunk.exception.FailWriteOpenException;
import org.junit.Assert;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com>Brent Douglas</a>
 * @since 1.0
 */
public class ExpectFailWriteOpenExceptionEventOrderListener extends EventOrderListener {

    @Override
    public void onError(final Exception exception) throws Exception {
        super.onError(exception);
        Assert.assertEquals(FailWriteOpenException.class, exception.getClass());
    }
}
