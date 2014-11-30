package io.machinecode.chainlink.jsl.core.inherit;

import io.machinecode.chainlink.spi.Mergeable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface MergeableList<T extends MergeableList<T>> extends Mergeable<T> {

    boolean getMerge();

    T setMerge(final boolean merge);
}
