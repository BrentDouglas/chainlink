package io.machinecode.nock.spi.inject;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Injector {

    <T> T inject(T that, Class<T> clazz);
}
