package io.machinecode.nock.spi.transport;

import io.machinecode.nock.spi.Repository;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface TransportFactory {

    Transport produce(Repository repository);
}
