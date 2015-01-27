package io.machinecode.chainlink.spi.jsl.inherit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface MergeableList<T extends MergeableList<T>> extends Mergeable<T> {

    boolean getMerge();

    T setMerge(final boolean merge);
}
