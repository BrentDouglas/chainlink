package io.machinecode.nock.spi.util;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Pair<K,V> {

    K getKey();

    V getValue();
}
