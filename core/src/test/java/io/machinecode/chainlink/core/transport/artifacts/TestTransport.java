package io.machinecode.chainlink.core.transport.artifacts;

import io.machinecode.chainlink.core.transport.DistributedTransport;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.core.transport.cmd.Command;
import io.machinecode.then.api.Promise;
import io.machinecode.then.core.RejectedDeferred;
import io.machinecode.then.core.ResolvedDeferred;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
public class TestTransport extends DistributedTransport<String> {

    final ConcurrentMap<String, TestTransport> transports;
    final String local;
    final List<String> remotes;

    public TestTransport(final ConcurrentMap<String, TestTransport> transports, final String local, final List<String> remotes, final Dependencies dependencies, final java.util.Properties properties) throws Exception {
        super(dependencies, properties);
        this.transports = transports;
        this.local = local;
        this.remotes = remotes;
    }

    @Override
    public String getAddress() {
        return local;
    }

    @Override
    protected List<String> getRemotes() {
        return remotes;
    }

    @Override
    public <T> Promise<T, Throwable, Object> invokeRemote(final Object address, final Command<T> command, final long timeout, final TimeUnit unit) {
        if (!(address instanceof String)) {
            return new RejectedDeferred<T, Throwable,Object>(new IllegalStateException("Should get a string"));
        }
        try {
            return new ResolvedDeferred<>(
                    transports.get(address)
                            .invokeLocal(command, getAddress())
                            .get(timeout, unit)
            );
        } catch (final Throwable e) {
            return new RejectedDeferred<>(e);
        }
    }
}
