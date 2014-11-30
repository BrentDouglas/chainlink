package io.machinecode.chainlink.spi;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface Mergeable<T extends Mergeable<T>> extends Copyable<T> {

    /**
     * {@inheritDoc}
     */
    T copy();

    /**
     * {@inheritDoc}
     */
    T copy(final T that);

    /**
     * Merges {@param that}.into this
     * @return this with that mixed in.
     */
    T merge(final T that);
}
