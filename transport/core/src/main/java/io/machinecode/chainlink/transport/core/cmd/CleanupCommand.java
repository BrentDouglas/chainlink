package io.machinecode.chainlink.transport.core.cmd;

import io.machinecode.chainlink.transport.core.DistributedRegistry;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class CleanupCommand<A,R extends DistributedRegistry<A,R>> implements DistributedCommand<Void,A,R> {

    final long jobExecutionId;

    public CleanupCommand(final long jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }

    @Override
    public Void perform(final R registry, final A origin) throws Throwable {
        registry.unregisterJob(jobExecutionId).get();
        return null;
    }
}
