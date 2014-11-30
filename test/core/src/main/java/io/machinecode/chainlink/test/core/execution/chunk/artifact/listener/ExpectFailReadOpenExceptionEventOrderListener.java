package io.machinecode.chainlink.test.core.execution.chunk.artifact.listener;

import io.machinecode.chainlink.test.core.execution.chunk.artifact.exception.FailReadOpenException;
import org.junit.Assert;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ExpectFailReadOpenExceptionEventOrderListener extends EventOrderListener {

    @Override
    public void onError(final Exception exception) throws Exception {
        super.onError(exception);
        Assert.assertEquals(FailReadOpenException.class, exception.getClass());
    }
}
