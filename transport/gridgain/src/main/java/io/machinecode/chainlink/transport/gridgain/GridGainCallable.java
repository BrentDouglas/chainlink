package io.machinecode.chainlink.transport.gridgain;

import io.machinecode.chainlink.transport.core.cmd.DistributedCommand;
import org.gridgain.grid.Grid;
import org.gridgain.grid.GridGain;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class GridGainCallable<T> implements Callable<T>, Serializable {
    private static final long serialVersionUID = 1L;

    final DistributedCommand<T,UUID, GridGainRegistry> command;
    final UUID origin;
    final String gridName;

    public GridGainCallable(final DistributedCommand<T, UUID, GridGainRegistry> command, final UUID origin, final Grid grid) {
        this.command = command;
        this.origin = origin;
        this.gridName = grid.configuration().getGridName();
    }

    @Override
    public T call() throws Exception {
        try {
            final GridGainRegistry registry = GridGain.grid(gridName).<String, GridGainRegistry>nodeLocalMap().get(GridGainRegistry.class.getName());
            return command.perform(registry, origin);
        } catch (final Throwable e) {
            throw new Exception(e);
        }
    }
}
