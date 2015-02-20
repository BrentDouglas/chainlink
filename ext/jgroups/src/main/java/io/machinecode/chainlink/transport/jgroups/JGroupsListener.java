package io.machinecode.chainlink.transport.jgroups;

import io.machinecode.chainlink.core.transport.cmd.Command;
import io.machinecode.then.core.DeferredImpl;
import io.machinecode.then.core.FutureDeferred;
import org.jboss.logging.Logger;
import org.jgroups.Address;
import org.jgroups.util.FutureListener;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JGroupsListener<T,P> extends DeferredImpl<T,Throwable,P> implements FutureListener<T> {
    private static final Logger log = Logger.getLogger(JGroupsListener.class);

    final Address local;
    final Address address;
    final Command<T> command;

    final long timeout;
    final TimeUnit unit;

    public JGroupsListener(final Address local, final Address address, final Command<T> command, final long timeout, final TimeUnit unit) {
        this.local = local;
        this.address = address;
        this.command = command;
        this.timeout = timeout;
        this.unit = unit;
    }

    @Override
    public void futureDone(final Future<T> future) {
        FutureDeferred.getFuture(future, this, timeout, unit);
    }

    @Override
    public void reject(final Throwable fail) {
        log.tracef("Node %s received reject from %s: %s %s.", local, address, command, fail);
        super.reject(fail);
    }

    @Override
    public void resolve(final T that) {
        log.tracef("Node %s received resolve from %s: %s %s.", local, address, command, that);
        super.resolve(that);
    }

    @Override
    public boolean cancel(final boolean mayInterrupt) {
        log.tracef("Node %s received cancel from %s: %s.", local, address, command);
        return super.cancel(mayInterrupt);
    }
}
