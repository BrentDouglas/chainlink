package io.machinecode.chainlink.transport.jgroups;

import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.ExecutableEvent;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.transport.jgroups.cmd.ExecuteCommand;
import io.machinecode.chainlink.transport.jgroups.cmd.PushChainCommand;
import io.machinecode.then.api.OnResolve;
import io.machinecode.then.api.Promise;
import io.machinecode.then.core.PromiseImpl;
import org.jgroups.Address;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JGroupsWorker implements Worker {

    final JGroupsRegistry registry;
    final Address local;
    final Address remote;
    final WorkerId workerId;

    public JGroupsWorker(final JGroupsRegistry registry, final Address local, final Address remote, final WorkerId workerId) {
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
        final ExecuteCommand command = new ExecuteCommand(workerId, event);
        registry.invoke(remote, command, new PromiseImpl<ChainAndId,Throwable>());
    }

    @Override
    public Promise<ChainAndId,Throwable> chain(final Executable executable) {
        final long jobExecutionId = executable.getContext().getJobExecutionId();
        final Promise<ChainAndId,Throwable> promise = new PromiseImpl<ChainAndId,Throwable>();
        final ChainId localId = registry.generateChainId();
        registry.invoke(
                remote,
                new PushChainCommand(jobExecutionId, localId),
                new PromiseImpl<ChainId,Throwable>()
                        .onResolve(new OnResolve<ChainId>() {
                            @Override
                            public void resolve(final ChainId remoteId) {
                                //This side has a different id than the remote side
                                promise.resolve(new ChainAndId(localId, remoteId, new JGroupsLocalChain(registry, remote, jobExecutionId, remoteId)));
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
