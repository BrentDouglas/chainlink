package io.machinecode.chainlink.transport.infinispan;

import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.ExecutableEvent;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.transport.infinispan.cmd.ExecuteCommand;
import io.machinecode.chainlink.transport.infinispan.cmd.PushChainCommand;
import io.machinecode.then.api.Deferred;
import io.machinecode.then.api.OnResolve;
import io.machinecode.then.api.Promise;
import io.machinecode.then.core.DeferredImpl;
import org.infinispan.remoting.transport.Address;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class InfinispanWorker implements Worker {

    final InfinispanRegistry registry;
    //final Address local;
    final Address remote;
    final WorkerId workerId;

    public InfinispanWorker(final InfinispanRegistry registry, final Address local, final Address remote, final WorkerId workerId) {
        this.registry = registry;
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
        final ExecuteCommand command = new ExecuteCommand(registry.cacheName, workerId, event);
        registry.invoke(remote, command, new DeferredImpl<ChainAndId,Throwable,Void>());
        /*
        registry.invoke(remote, command, new DeferredImpl<ChainAndId,Throwable>().onReject(new OnReject<Throwable>() {
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
                    registry.invoke(remote, command, new DeferredImpl<Object, Throwable>());
                    //If this fails a second time execution will halt until the jgroups request times out
                }
            }
        }));
        */
    }

    @Override
    public Promise<ChainAndId,Throwable,?> chain(final Executable executable) {
        final long jobExecutionId = executable.getContext().getJobExecutionId();
        final Deferred<ChainAndId,Throwable,Void> promise = new DeferredImpl<>();
        final ChainId localId = registry.generateChainId();
        registry.invoke(
                remote,
                new PushChainCommand(registry.cacheName, jobExecutionId, localId),
                new DeferredImpl<ChainId,Throwable,Void>()
                        .onResolve(new OnResolve<ChainId>() {
                            @Override
                            public void resolve(final ChainId remoteId) {
                                //This side has a different id than the remote side
                                promise.resolve(new ChainAndId(localId, remoteId, new InfinispanLocalChain(registry, remote, jobExecutionId, remoteId)));
                            }
                        }).onReject(promise)
        );
        return promise;
    }

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
