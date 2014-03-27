package io.machinecode.chainlink.transport.infinispan;

import io.machinecode.chainlink.jsl.core.util.ImmutablePair;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.util.Pair;
import io.machinecode.chainlink.transport.infinispan.cmd.CreateDeferredCommand;
import io.machinecode.chainlink.transport.infinispan.cmd.ExecuteCommand;
import io.machinecode.chainlink.spi.transport.DeferredId;
import io.machinecode.chainlink.spi.transport.WorkerId;
import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.execution.ExecutableEvent;
import io.machinecode.chainlink.spi.execution.Worker;
import org.infinispan.remoting.transport.Address;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class RpcWorker implements Worker {

    final InfinispanTransport transport;
    final Address local;
    final Address remote;
    final WorkerId workerId;

    public RpcWorker(final InfinispanTransport transport, final Address local, final Address remote, final WorkerId workerId) {
        this.transport = transport;
        this.local = local;
        this.remote = remote;
        this.workerId = workerId;
    }

    @Override
    public WorkerId getWorkerId() {
        return workerId;
    }

    @Override
    public void addExecutable(final ExecutableEvent event) {
        transport.invokeSync(local, new ExecuteCommand(transport.cacheName, remote, workerId, event));
    }

    @Override
    public Pair<DeferredId, Deferred<?>> createDistributedDeferred(final Executable executable) {
        final long jobExecutionId = executable.getContext().getJobExecutionId();
        final DeferredId deferredId = (DeferredId)transport.invokeSync(remote, new CreateDeferredCommand(transport.cacheName, executable));
        return ImmutablePair.<DeferredId, Deferred<?>>of(deferredId, new RemoteDeferred(transport, remote, jobExecutionId, deferredId));
    }

    @Override
    public void startup() {
        //no-op
    }

    @Override
    public void shutdown() {
        //no-op
    }

    @Override
    public void run() {
        //no-op
    }
}
