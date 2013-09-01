package io.machinecode.nock.core.local;

import io.machinecode.nock.spi.Repository;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.transport.TransportFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class LocalTransportFactory implements TransportFactory {

    final ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public Transport produce(final Repository repository) {
        return new LocalTransport(repository, executor);
    }
}
