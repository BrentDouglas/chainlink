package io.machinecode.nock.jsl.inherit;

import io.machinecode.nock.spi.Mergeable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface MergeableList<T extends MergeableList<T>> extends Mergeable<T> {

    boolean getMerge();

    T setMerge(final boolean merge);
}
