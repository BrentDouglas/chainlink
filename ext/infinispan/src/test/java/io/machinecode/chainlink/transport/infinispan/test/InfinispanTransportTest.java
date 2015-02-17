package io.machinecode.chainlink.transport.infinispan.test;

import io.machinecode.chainlink.core.transport.TestTransportFactory;
import io.machinecode.chainlink.core.transport.TransportTest;
import org.junit.After;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class InfinispanTransportTest extends TransportTest {
    @Override
    protected TestTransportFactory createFactory() {
        return new TestInfinispanTransportFactory();
    }

    @After
    public void after() throws Exception {
        super.after();
        firstFactory.close();
        secondFactory.close();
        thirdFactory.close();
    }
}
