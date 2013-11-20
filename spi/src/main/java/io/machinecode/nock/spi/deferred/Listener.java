package io.machinecode.nock.spi.deferred;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Listener {

    void run(final Deferred<?,?> that);
}
