package io.machinecode.nock.spi.transport;

/**
 * Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Synchronization {

    void register();

    int registered();

    void unRegister();
}
