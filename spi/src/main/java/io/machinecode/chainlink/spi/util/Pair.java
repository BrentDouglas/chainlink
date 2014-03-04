package io.machinecode.chainlink.spi.util;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Pair<K,V> {

    K getName();

    V getValue();
}
