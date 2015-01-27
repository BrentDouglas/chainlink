package io.machinecode.chainlink.transport.gridgain.test;

import io.machinecode.chainlink.core.transport.TestTransportFactory;
import io.machinecode.chainlink.core.transport.TransportTest;
import org.gridgain.grid.GridException;
import org.junit.AfterClass;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class GridGainTransportTest extends TransportTest {

    @Override
    protected TestTransportFactory createFactory() throws GridException {
        return new TestGridGainTransportFactory();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        firstFactory.close();
        secondFactory.close();
        thirdFactory.close();
    }
}
