package io.machinecode.chainlink.transport.gridgain;

import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.core.transport.cmd.Command;
import org.gridgain.grid.Grid;
import org.gridgain.grid.resources.GridInstanceResource;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class GridGainCallable<T> implements Callable<T>, Serializable {
    private static final long serialVersionUID = 1L;

    final Command<T> command;
    final UUID origin;

    @GridInstanceResource
    private transient Grid grid;

    public GridGainCallable(final Command<T> command, final UUID origin) {
        this.command = command;
        this.origin = origin;
    }

    @Override
    public T call() throws Exception {
        try {
            final Configuration configuration = this.grid.<String, Configuration>nodeLocalMap().get(Configuration.class.getName());
            return command.perform(configuration, origin);
        } catch (final Exception e) {
            throw e;
        } catch (final Throwable e) {
            throw new Exception(e);
        }
    }
}
