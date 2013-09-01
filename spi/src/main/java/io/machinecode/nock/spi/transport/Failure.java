package io.machinecode.nock.spi.transport;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Failure {

    void fail(Transport transport, Exception exception);
}
