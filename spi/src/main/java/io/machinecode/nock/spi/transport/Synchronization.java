package io.machinecode.nock.spi.transport;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Synchronization {

    void enlist();

    void delist();

    boolean available();
}
