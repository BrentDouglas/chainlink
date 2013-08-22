package io.machinecode.nock.spi.inject;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Resolvable<T> {

    T resolve(final ClassLoader loader) throws Exception;
}
