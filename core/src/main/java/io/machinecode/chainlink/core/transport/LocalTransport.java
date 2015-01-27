package io.machinecode.chainlink.core.transport;

import io.machinecode.chainlink.core.transport.cmd.Command;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.then.api.Promise;
import io.machinecode.then.core.RejectedDeferred;
import io.machinecode.then.core.ResolvedDeferred;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class LocalTransport extends BaseTransport<Void> {

    public LocalTransport(final Dependencies dependencies, final Properties properties) {
        super(dependencies, properties);
    }

    @Override
    public <T> Promise<? extends Iterable<T>,Throwable,Object> invokeEverywhere(final Command<T> command, final long timeout, final TimeUnit unit) {
        try {
            return new ResolvedDeferred<Iterable<T>, Throwable, Object>(Collections.singletonList(command.perform(this.configuration, getAddress())));
        } catch (final Throwable e) {
            return new RejectedDeferred<>(e);
        }
    }

    @Override
    public <T> Promise<T, Throwable,Object> invokeRemote(final Object address, final Command<T> command, final long timeout, final TimeUnit unit) {
        try {
            return new ResolvedDeferred<>(command.perform(this.configuration, getAddress()));
        } catch (final Throwable e) {
            return new RejectedDeferred<>(e);
        }
    }

    @Override
    public Void getAddress() {
        return null;
    }

    @Override
    public long getTimeout() {
        return 0;
    }

    @Override
    public TimeUnit getTimeUnit() {
        return TimeUnit.MILLISECONDS;
    }
}
