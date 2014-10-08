package io.machinecode.chainlink.transport.coherence;

import com.tangosol.net.Member;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.transport.core.DistributedProxyExecutionRepository;
import io.machinecode.chainlink.transport.core.cmd.InvokeExecutionRepositoryCommand;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CoherenceProxyExecutionRepository extends DistributedProxyExecutionRepository<Member,CoherenceRegistry> {

    public CoherenceProxyExecutionRepository(final CoherenceRegistry registry, final ExecutionRepositoryId executionRepositoryId, final Member address) {
        super(registry, executionRepositoryId, address);
    }

    @Override
    protected <T> InvokeExecutionRepositoryCommand<T,Member,CoherenceRegistry> _cmd(final String name, final Serializable... params) {
        return new InvokeExecutionRepositoryCommand<T,Member,CoherenceRegistry>(executionRepositoryId, name, params);
    }
}
