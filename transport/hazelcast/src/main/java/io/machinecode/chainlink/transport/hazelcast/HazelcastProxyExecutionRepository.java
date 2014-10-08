package io.machinecode.chainlink.transport.hazelcast;

import com.hazelcast.core.Member;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.transport.core.DistributedProxyExecutionRepository;
import io.machinecode.chainlink.transport.core.cmd.InvokeExecutionRepositoryCommand;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class HazelcastProxyExecutionRepository extends DistributedProxyExecutionRepository<Member,HazelcastRegistry> {

    public HazelcastProxyExecutionRepository(final HazelcastRegistry registry, final ExecutionRepositoryId executionRepositoryId, final Member address) {
        super(registry, executionRepositoryId, address);
    }

    @Override
    protected <T> InvokeExecutionRepositoryCommand<T,Member,HazelcastRegistry> _cmd(final String name, final Serializable... params) {
        return new InvokeExecutionRepositoryCommand<T,Member,HazelcastRegistry>(executionRepositoryId, name, params);
    }
}
