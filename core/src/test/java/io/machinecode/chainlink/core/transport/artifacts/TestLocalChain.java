package io.machinecode.chainlink.core.transport.artifacts;

import io.machinecode.chainlink.core.transport.DistributedLocalChain;
import io.machinecode.chainlink.core.transport.cmd.InvokeChainCommand;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.transport.Command;
import io.machinecode.chainlink.spi.transport.Transport;

import java.io.Serializable;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
public class TestLocalChain extends DistributedLocalChain<String> {

    public TestLocalChain(final Transport<String> transport, final String address, final long jobExecutionId, final ChainId chainId) {
        super(transport, address, jobExecutionId, chainId);
    }

    @Override
    protected <T> Command<T, String> command(final String name, final Serializable... params) {
        return new InvokeChainCommand<>(jobExecutionId, chainId, name, params);
    }
}
