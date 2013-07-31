package io.machinecode.nock.jsl.xml.util;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
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
    public abstract T merge(final T that);
}
