package io.machinecode.nock.spi.transport;

/**
 * Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Synchronization {

    void take();

    boolean available();

    void release();

    void listener(final Object that);
}
