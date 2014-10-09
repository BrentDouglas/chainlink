package io.machinecode.chainlink.transport.coherence;

import com.tangosol.net.Member;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.transport.core.DistributedProxyExecutionRepository;
import io.machinecode.chainlink.transport.core.cmd.InvokeExecutionRepositoryCommand;

import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class CoherenceProxyExecutionRepository extends DistributedProxyExecutionRepository<Member> {

    public CoherenceProxyExecutionRepository(final CoherenceTransport registry, final ExecutionRepositoryId executionRepositoryId, final Member address) {
        super(registry, executionRepositoryId, address);
    }

    @Override
    protected <T> InvokeExecutionRepositoryCommand<T,Member> _cmd(final String name, final Serializable... params) {
        return new InvokeExecutionRepositoryCommand<>(executionRepositoryId, name, params);
    }
}
