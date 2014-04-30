package io.machinecode.chainlink.transport.infinispan;

import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.transport.infinispan.cmd.CreateChainCommand;
import io.machinecode.chainlink.transport.infinispan.cmd.ExecuteCommand;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.execution.ExecutableEvent;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.then.api.OnResolve;
import io.machinecode.then.api.Promise;
import io.machinecode.then.core.PromiseImpl;
import org.infinispan.remoting.transport.Address;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class RemoteWorker implements Worker {

    final InfinispanRegistry registry;
    final Address local;
    final Address remote;
    final WorkerId workerId;

    public RemoteWorker(final InfinispanRegistry registry, final Address local, final Address remote, final WorkerId workerId) {
        this.registry = registry;
        this.local = local;
        this.remote = remote;
        this.workerId = workerId;
    }

    @Override
    public WorkerId id() {
        return workerId;
    }

    @Override
    public void execute(final ExecutableEvent event) {
        registry.invoke(remote, new ExecuteCommand(registry.cacheName, workerId, event), new PromiseImpl<Object>());
    }

    @Override
    public Promise<ChainAndId> chain(final Executable executable) {
        final long jobExecutionId = executable.getContext().getJobExecutionId();
        final Promise<ChainAndId> promise = new PromiseImpl<ChainAndId>();
        final ChainId localId = registry.generateChainId();
        registry.invoke(
                remote,
                new CreateChainCommand(registry.cacheName, jobExecutionId, localId),
                new PromiseImpl<ChainId>()
                        .onResolve(new OnResolve<ChainId>() {
                            @Override
                            public void resolve(final ChainId remoteId) {
                                //This side has a different id than the remote side
                                promise.resolve(new ChainAndId(localId, remoteId, new RemoteChain(registry, remote, jobExecutionId, remoteId)));
                            }
                        }).onReject(promise)
        );
        return promise;
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
