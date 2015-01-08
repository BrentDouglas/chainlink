package io.machinecode.chainlink.core.transport;

import io.machinecode.chainlink.core.base.BaseTest;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.transport.Transport;
import org.junit.Test;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class TransportTest extends BaseTest {

    @Test
    public void generatedIdsTest() throws Exception {
        final Transport<?> transport = configuration().getTransport();

        {
            final ChainId first = transport.generateChainId();
            final ChainId second = transport.generateChainId();
            assertNotNull(first);
            assertNotNull(second);
            assertFalse(first.equals(second));
        }
        {
            final ExecutableId first = transport.generateExecutableId();
            final ExecutableId second = transport.generateExecutableId();
            assertNotNull(first);
            assertNotNull(second);
            assertFalse(first.equals(second));
        }
        {
            final ExecutionRepositoryId first = transport.generateExecutionRepositoryId();
            final ExecutionRepositoryId second = transport.generateExecutionRepositoryId();
            assertNotNull(first);
            assertNotNull(second);
            assertFalse(first.equals(second));
        }
    }
}
