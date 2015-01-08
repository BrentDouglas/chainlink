package io.machinecode.chainlink.core.transport.artifacts;

import io.machinecode.chainlink.core.transport.DistributedProxyExecutionRepository;
import io.machinecode.chainlink.core.transport.DistributedTransport;
import io.machinecode.chainlink.core.transport.DistributedWorker;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.transport.Command;
import io.machinecode.then.api.Deferred;
import io.machinecode.then.core.FutureDeferred;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
public class TestTransport extends DistributedTransport<String> {

    final ConcurrentMap<String, TestTransport> transports;
    final String local;
    final List<String> remotes;
    final ExecutorService executor = Executors.newSingleThreadExecutor();

    public TestTransport(final ConcurrentMap<String, TestTransport> transports, final String local, final List<String> remotes, final Dependencies dependencies, final java.util.Properties properties) throws Exception {
        super(dependencies, properties);
        this.transports = transports;
        this.local = local;
        this.remotes = remotes;
    }

    @Override
    public String getLocal() {
        return local;
    }

    @Override
    protected List<String> getRemotes() {
        return remotes;
    }

    @Override
    protected DistributedWorker<String> createDistributedWorker(final String address, final WorkerId workerId) {
        return new TestWorker(this, this.getLocal(), address, workerId);
    }

    @Override
    protected DistributedProxyExecutionRepository<String> createDistributedExecutionRepository(final ExecutionRepositoryId id, final String address) {
        return new TestRepositoryProxy(this, id, address);
    }

    @Override
    protected boolean isMatchingAddressType(final Object address) {
        return address instanceof String;
    }

    @Override
    public <T> void invokeRemote(final String address, final Command<T, String> command, final Deferred<T, Throwable, ?> promise, final long timeout, final TimeUnit unit) {
        try {
            promise.resolve(
                    transports.get(address)
                            .invokeLocal(command, getLocal())
                            .get(timeout, unit)
            );
        } catch (final Exception e) {
            promise.reject(e);
        }
    }
}
