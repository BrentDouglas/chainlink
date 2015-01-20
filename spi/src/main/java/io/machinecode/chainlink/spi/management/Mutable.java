package io.machinecode.chainlink.spi.management;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Mutable<T> {

    boolean willAccept(final T that);

    void accept(final T from, final Op... ops) throws Exception;
}
