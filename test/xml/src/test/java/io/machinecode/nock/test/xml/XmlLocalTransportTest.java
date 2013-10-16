package io.machinecode.nock.test.xml;

import io.machinecode.nock.core.configuration.RuntimeConfigurationImpl;
import io.machinecode.nock.test.core.transport.LocalTransportTest;
import org.junit.BeforeClass;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class XmlLocalTransportTest extends LocalTransportTest {

    @BeforeClass
    public static void beforeClass() {
        configuration = new RuntimeConfigurationImpl(configuration().build());
    }
}
