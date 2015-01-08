package io.machinecode.chainlink.core.transport;

import io.machinecode.chainlink.core.transport.cmd.CallbackCommand;
import io.machinecode.chainlink.core.transport.cmd.ExecuteCommand;
import io.machinecode.chainlink.spi.execution.CallbackEvent;
import io.machinecode.chainlink.spi.execution.ChainAndIds;
import io.machinecode.chainlink.spi.execution.ExecutableEvent;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.transport.Command;
import io.machinecode.chainlink.spi.transport.Transport;
import io.machinecode.then.api.Deferred;
import io.machinecode.then.api.OnResolve;
import io.machinecode.then.api.Promise;
import io.machinecode.then.core.DeferredImpl;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public abstract class DistributedWorker<A> implements Worker {

    protected final Transport<A> transport;
    //protected final A local;
    protected final A remote;
    protected final WorkerId workerId;

    public DistributedWorker(final Transport<A> transport, final A local, final A remote, final WorkerId workerId) {
        this.transport = transport;
        //this.local = local;
        this.remote = remote;
        this.workerId = workerId;
    }

    @Override
    public WorkerId id() {
        return workerId;
    }

    @Override
    public void execute(final ExecutableEvent event) {
        final Command<Object, A> command = new ExecuteCommand<>(workerId, event);
        transport.invokeRemote(remote, command, new DeferredImpl<Object, Throwable,Void>(), transport.getTimeout(), transport.getTimeUnit());
    }

    @Override
    public void callback(final CallbackEvent event) {
        final Command<Object, A> command = new CallbackCommand<>(workerId, event);
        transport.invokeRemote(remote, command, new DeferredImpl<Object, Throwable,Void>(), transport.getTimeout(), transport.getTimeUnit());
    }

    @Override
    public Promise<ChainAndIds,Throwable,?> chain(final long jobExecutionId) {
        final Deferred<ChainAndIds,Throwable,?> promise = new DeferredImpl<ChainAndIds, Throwable, Void>();
        final ChainId localId = transport.generateChainId();
        transport.invokeRemote(
                remote,
                createPushChainCommand(jobExecutionId, localId),
                new DeferredImpl<ChainId, Throwable,Void>()
                        .onResolve(new OnResolve<ChainId>() {
                            @Override
                            public void resolve(final ChainId remoteId) {
                                //This side has a different id than the remote side
                                promise.resolve(new ChainAndIds(localId, remoteId, createLocalChain(jobExecutionId, remoteId)));
                            }
                        }).onReject(promise),
                transport.getTimeout(),
                transport.getTimeUnit()
        );
        return promise;
    }

    protected abstract Command<ChainId, A> createPushChainCommand(final long jobExecutionId, final ChainId localId);

    protected abstract Chain<?> createLocalChain(final long jobExecutionId, final ChainId remoteId);

    @Override
    public void start() {
        //no-op
    }

    @Override
    public void close() {
        //no-op
    }

    @Override
    public void run() {
        //no-op
    }
}
