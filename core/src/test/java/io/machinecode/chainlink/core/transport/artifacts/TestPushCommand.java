package io.machinecode.chainlink.core.transport.artifacts;

import io.machinecode.chainlink.core.transport.DistributedRemoteChain;
import io.machinecode.chainlink.core.transport.cmd.PushChainCommand;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.transport.Transport;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
public class TestPushCommand extends PushChainCommand<String> {

    public TestPushCommand(final long jobExecutionId, final ChainId chainId) {
        super(jobExecutionId, chainId);
    }

    @Override
    protected DistributedRemoteChain<String> createRemoteChain(final Transport<String> transport, final String address) {
        return new TestRemoteChain(transport, address, jobExecutionId, chainId);
    }
}
