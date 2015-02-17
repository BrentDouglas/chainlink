package io.machinecode.chainlink.transport.jgroups.test;

import io.machinecode.chainlink.core.transport.TestTransportFactory;
import io.machinecode.chainlink.core.transport.TransportTest;
import org.junit.AfterClass;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JGroupsTransportTest extends TransportTest {

    @Override
    protected TestTransportFactory createFactory() throws Exception {
        return new TestJGroupsTransportFactory();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        firstFactory.close();
        secondFactory.close();
        thirdFactory.close();
    }
}
