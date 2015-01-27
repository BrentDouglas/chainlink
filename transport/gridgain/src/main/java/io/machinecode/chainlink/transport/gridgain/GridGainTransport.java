package io.machinecode.chainlink.transport.gridgain;

import io.machinecode.chainlink.core.transport.DistributedTransport;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.core.transport.cmd.Command;
import io.machinecode.then.api.Promise;
import io.machinecode.then.core.FutureDeferred;
import io.machinecode.then.core.RejectedDeferred;
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
    public void open(final Configuration configuration) throws Exception {
        super.open(configuration);
        grid.nodeLocalMap().addIfAbsent(Configuration.class.getName(), configuration);
    }

    @Override
    public void close() throws Exception {
        log.infof("GridGainRegistry is shutting down."); //TODO Message
        super.close();
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
    public <T> Promise<T,Throwable,Object> invokeRemote(final Object address, final Command<T> command,
                                 final long timeout, final TimeUnit unit) {
        if (!(address instanceof UUID)) {
            return new RejectedDeferred<T, Throwable, Object>(new Exception("Expected " + UUID.class.getName() + ". Found " + address.getClass())); //TODO Message
        }
        final UUID uuid = (UUID) address;
        try {
            log.tracef("Invoking %s on %s.", command, address);
            final GridFuture<T> future = this.grid.forNodeId(uuid)
                    .compute()
                    .call(new GridGainCallable<>(command, getAddress()));
            return new FutureDeferred<>(new GridGainFuture<>(future), timeout, unit);
        } catch (final Throwable e) {
            return new RejectedDeferred<>(e);
        }
    }

    @Override
    protected <T> Promise<? extends Iterable<T>,Throwable,Object> invokeEverywhere(final Command<T> command, final long timeout, final TimeUnit unit) {
        log.tracef("Invoking %s on all remotes.", command);
        final GridFuture<Collection<T>> future = this.grid.forRemotes()
                .compute()
                .broadcast(new GridGainCallable<>(command, getAddress()));
        final FutureDeferred<Collection<T>,Object> run = new FutureDeferred<>(new GridGainFuture<>(future), timeout, unit);
        network.execute(run);
        return run;
    }
}
