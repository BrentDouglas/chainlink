package io.machinecode.chainlink.marshalling.jboss.test;

import io.machinecode.chainlink.core.marshalling.MarshallingTest;
import io.machinecode.chainlink.marshalling.jboss.JbossMarshallingFactory;
import io.machinecode.chainlink.spi.marshalling.Marshalling;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JbossMarshallingTest extends MarshallingTest {

    @Override
    public Marshalling create() throws Exception {
        return new JbossMarshallingFactory().produce(null, null);
    }
}
