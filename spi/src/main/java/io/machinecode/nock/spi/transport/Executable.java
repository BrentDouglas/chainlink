package io.machinecode.nock.spi.transport;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Executable extends Future<Void>, Synchronization, Serializable {

    Executable register(Synchronization synchronization);

    void execute(Transport transport);
}
