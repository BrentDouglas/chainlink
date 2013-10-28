package io.machinecode.nock.spi.work;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Listener {

    void run(final Deferred<?> that);
}
