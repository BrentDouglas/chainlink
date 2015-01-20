package io.machinecode.chainlink.transport.gridgain;

import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.transport.Command;
import io.machinecode.chainlink.core.transport.DistributedProxyExecutionRepository;
import io.machinecode.chainlink.core.transport.DistributedTransport;
import io.machinecode.chainlink.core.transport.DistributedWorker;
import io.machinecode.then.api.Deferred;
import io.machinecode.then.core.FutureDeferred;
import org.gridgain.grid.Grid;
import org.gridgain.grid.GridFuture;
import org.gridgain.grid.GridNode;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class GridGainTransport extends DistributedTransport<UUID> {

    private static final Logger log = Logger.getLogger(GridGainTransport.class);

    final Grid grid;
    final UUID local;

    public GridGainTransport(final Dependencies dependencies, final Properties properties, final Grid grid) throws Exception {
        super(dependencies, properties);
        this.grid = grid;
        this.local = grid.localNode().id();
        grid.nodeLocalMap().addIfAbsent(GridGainTransport.class.getName(), this);
        log.infof("GridGainRegistry started on address: [%s]", this.local); //TODO Message
    }

    protected List<UUID> _remoteMemberIdsFromNodes(final Collection<GridNode> all) {
        final List<UUID> that = new ArrayList<>(all.size());
        for (final GridNode node : all) {
            that.add(node.id());
        }
        that.remove(this.getAddress());
        return Collections.unmodifiableList(that);
    }

    @Override
    public void close() throws Exception {
        log.infof("GridGainRegistry is shutting down."); //TODO Message
        try {
            this.grid.close();
        } finally {
            super.close();
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
    public UUID getAddress() {
        return local;
    }

    @Override
    protected List<UUID> getRemotes() {
        return _remoteMemberIdsFromNodes(this.grid.nodes());
    }

    @Override
    protected DistributedWorker<UUID> createDistributedWorker(final UUID address, final WorkerId workerId) {
        return new GridGainWorker(this, this.local, address, workerId);
    }

    @Override
    protected DistributedProxyExecutionRepository<UUID> createDistributedExecutionRepository(final ExecutionRepositoryId id, final UUID address) {
        return new GridGainProxyExecutionRepository(this, id, address);
    }

    @Override
    protected boolean isMatchingAddressType(final Object address) {
        return address instanceof UUID;
    }

    @Override
    public <T> void invokeRemote(final UUID address, final Command<T, UUID> command,
                                 final Deferred<T, Throwable,?> promise, final long timeout, final TimeUnit unit) {
        try {
            log.tracef("Invoking %s on %s.", command, address);
            final GridFuture<T> future = this.grid.forNodeId(address)
                    .compute()
                    .call(new GridGainCallable<>(command, getAddress(), grid));
            final FutureDeferred<T, Void> run = new FutureDeferred<>(new GridGainFuture<>(future), timeout, unit);
            run.onResolve(promise)
                    .onReject(promise)
                    .onCancel(promise);
            network.execute(run);
        } catch (Exception e) {
            promise.reject(e);
        }
    }
}
