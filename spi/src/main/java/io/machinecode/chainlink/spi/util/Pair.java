package io.machinecode.chainlink.spi.util;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface Pair<K,V> {

    K getName();

    V getValue();
}
