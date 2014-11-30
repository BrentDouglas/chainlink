package io.machinecode.chainlink.transport.core;

import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.ExecutableEvent;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.transport.core.cmd.DistributedCommand;
import io.machinecode.chainlink.transport.core.cmd.ExecuteCommand;
import io.machinecode.then.api.Deferred;
import io.machinecode.then.api.OnResolve;
import io.machinecode.then.api.Promise;
import io.machinecode.then.core.DeferredImpl;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public abstract class DistributedWorker<A, R extends DistributedRegistry<A, R>> implements Worker {

    protected final R registry;
    protected final A local;
    protected final A remote;
    protected final WorkerId workerId;

    public DistributedWorker(final R registry, final A local, final A remote, final WorkerId workerId) {
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
        final DistributedCommand<Object, A, R> command = new ExecuteCommand<A, R>(workerId, event);
        registry.invoke(remote, command, new DeferredImpl<Object, Throwable, Void>());
        /*
        registry.invoke(remote, command, new DeferredImpl<ChainAndId,Throwable,Void>().onReject(new OnReject<Throwable>() {
            @Override
            public void reject(final Throwable fail) {
                if (fail instanceof java.io.ObjectStreamException
                        || fail instanceof org.infinispan.commons.marshall.NotSerializableException) {
                    final ExecutionContext context = event.getContext();
                    final MutableStepContext stepContext = context.getStepContext();
                    final MutableJobContext jobContext = context.getJobContext();
                    stepContext.setBatchStatus(BatchStatus.FAILED);
                    jobContext.setBatchStatus(BatchStatus.FAILED);
                    stepContext.setException((Exception)fail); //TODO Is this right?
                    // Ideally the only thing that should really be able to mess this up is the transient user data,
                    // persistent user data and the blobs returned from chunks.
                    stepContext.setTransientUserData(null);
                    stepContext.setPersistentUserData(null);
                    jobContext.setTransientUserData(null);
                    jobContext.setPersistentUserData(null);
                    final Item[] old = context.getItems();
                    if (old != null) {
                        final Item[] items = new Item[old.length];
                        for (int i = 0; i < old.length; ++i) {
                            final item item = old[i];
                            items[i] = new ItemImpl(null, item.getBatchStatus(), item.getExitStatus());
                        }
                        context.setItems(items);
                    }
                    registry.invoke(remote, command, new DeferredImpl<Object, Throwable,Void>());
                    //If this fails a second time execution will halt until the jgroups request times out
                }
            }
        }));
        */
    }

    @Override
    public Promise<Worker.ChainAndId,Throwable,?> chain(final Executable executable) {
        final long jobExecutionId = executable.getContext().getJobExecutionId();
        final Deferred<ChainAndId,Throwable,Void> promise = new DeferredImpl<ChainAndId, Throwable, Void>();
        final ChainId localId = registry.generateChainId();
        registry.invoke(
                remote,
                createPushChainCommand(jobExecutionId, localId),
                new DeferredImpl<ChainId, Throwable, Void>()
                        .onResolve(new OnResolve<ChainId>() {
                            @Override
                            public void resolve(final ChainId remoteId) {
                                //This side has a different id than the remote side
                                promise.resolve(new Worker.ChainAndId(localId, remoteId, createLocalChain(jobExecutionId, remoteId)));
                            }
                        }).onReject(promise)
        );
        return promise;
    }

    protected abstract DistributedCommand<ChainId, A, R> createPushChainCommand(final long jobExecutionId, final ChainId localId);

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
