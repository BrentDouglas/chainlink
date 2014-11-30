package io.machinecode.chainlink.spi;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface Copyable<T extends Copyable<T>> {

    /**
     * Instantiate a copy of T and make a deep copy.
     * @return A new instance of T as a deep copy of this
     */
    T copy();

    /**
     * Copy this into {@param that}.
     * @return {@param that} as a deep copy of this
     */
    T copy(final T that);
}
