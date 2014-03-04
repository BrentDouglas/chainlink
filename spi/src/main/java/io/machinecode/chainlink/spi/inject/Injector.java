package io.machinecode.chainlink.spi.inject;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Injector {

    <T> boolean inject(final T bean) throws Exception;
}
