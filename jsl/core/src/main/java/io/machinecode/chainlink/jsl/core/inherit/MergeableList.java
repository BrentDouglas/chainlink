package io.machinecode.chainlink.jsl.core.inherit;

import io.machinecode.chainlink.spi.Mergeable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface MergeableList<T extends MergeableList<T>> extends Mergeable<T> {

    boolean getMerge();

    T setMerge(final boolean merge);
}