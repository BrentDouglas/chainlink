package io.machinecode.chainlink.transport.gridgain;

import io.machinecode.chainlink.spi.configuration.RegistryConfiguration;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.transport.core.BaseDistributedRegistry;
import io.machinecode.chainlink.transport.core.DistributedProxyExecutionRepository;
import io.machinecode.chainlink.transport.core.DistributedRegistry;
import io.machinecode.chainlink.transport.core.DistributedWorker;
import io.machinecode.chainlink.transport.core.cmd.DistributedCommand;
import io.machinecode.then.api.OnResolve;
import io.machinecode.then.api.Promise;
import io.machinecode.then.core.PromiseImpl;
import org.gridgain.grid.Grid;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridFuture;
import org.gridgain.grid.GridNode;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class GridGainRegistry extends BaseDistributedRegistry<UUID,GridGainRegistry> implements DistributedRegistry<UUID,GridGainRegistry> {

    private static final Logger log = Logger.getLogger(GridGainRegistry.class);

    final Grid grid;
    final UUID local;

    public GridGainRegistry(final RegistryConfiguration configuration, final Grid grid) throws Exception {
        super(configuration);
        this.grid = grid;
        this.local = grid.localNode().id();
        grid.nodeLocalMap().addIfAbsent(GridGainRegistry.class.getName(), this);
    }

    protected List<UUID> _remoteMemberIdsFromNodes(final Collection<GridNode> all) {
        final List<UUID> that = new ArrayList<UUID>(all.size());
        for (final GridNode node : all) {
            that.add(node.id());
        }
        that.remove(this.getLocal());
        return Collections.unmodifiableList(that);
    }

    @Override
    public void startup() {
        super.startup();
        log.infof("GridGainRegistry started on address: [%s]", this.local); //TODO Message
    }

    @Override
    public void shutdown() {
        log.infof("GridGainRegistry is shutting down."); //TODO Message
        try {
            this.grid.close();
        } catch (final GridException e) {
            throw new RuntimeException(e);
        } finally {
            super.shutdown();
        }
    }

    @Override
    public ExecutableId generateExecutableId() {
        return new GridGainUUIDId(local);
    }

    @Override
    public WorkerId generateWorkerId(final Worker worker) {
        if (worker instanceof Thread) {
            return new GridGainWorkerId((Thread)worker, local);
        } else {
            return new GridGainUUIDId(local);
        }
    }

    @Override
    public ExecutionRepositoryId generateExecutionRepositoryId() {
        return new GridGainUUIDId(local);
    }

    @Override
    public UUID getLocal() {
        return local;
    }

    @Override
    protected List<UUID> getRemotes() {
        return _remoteMemberIdsFromNodes(this.grid.nodes());
    }

    @Override
    protected DistributedWorker<UUID, GridGainRegistry> createDistributedWorker(final UUID address, final WorkerId workerId) {
        return new GridGainWorker(this, this.local, address, workerId);
    }

    @Override
    protected DistributedProxyExecutionRepository<UUID, GridGainRegistry> createDistributedExecutionRepository(final ExecutionRepositoryId id, final UUID address) {
        return new GridGainProxyExecutionRepository(this, id, address);
    }

    @Override
    public <T> void invoke(final UUID address, final DistributedCommand<T, UUID, GridGainRegistry> command,
                           final Promise<T, Throwable> promise) {
        this.invoke(address, command, promise, timeout, unit);
    }

    @Override
    public <T> void invoke(final UUID address, final DistributedCommand<T, UUID, GridGainRegistry> command,
                           final Promise<T, Throwable> promise, final long timeout, final TimeUnit unit) {
        try {
            log.tracef("Invoking %s on %s.", command, address);
            final GridFuture<T> future = this.grid.forNodeId(address)
                    .compute()
                    .call(new GridGainCallable<T>(command, getLocal(), grid));
            network.when(
                    timeout, unit,
                    new GridGainFuture<T>(future),
                    new PromiseImpl<T, Throwable>().onResolve(new OnResolve<T>() {
                        @Override
                        public void resolve(final T ret) {
                            try {
                                promise.resolve(ret);
                            } catch (final Throwable e) {
                                promise.reject(e);
                            }
                        }
                    }).onReject(promise)
            );
        } catch (Exception e) {
            promise.reject(e);
        }
    }
}
