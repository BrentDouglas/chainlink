package io.machinecode.chainlink.jsl.core.inherit;

import io.machinecode.chainlink.jsl.core.util.ForwardingList;
import io.machinecode.chainlink.spi.Copyable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CopyList<T extends Copyable<T>> extends ForwardingList<T> implements List<T> {

    public CopyList(final List<? extends T> delegate) {
        super(delegate == null ? Collections.<T>emptyList() : new ArrayList<T>(delegate.size()));
        if (delegate == null) {
            return;
        }
        for (final T that : delegate) {
            this.delegate.add(that == null ? null : that.copy());
        }
    }

    public CopyList(final List<? extends T>... delegates) {
        super(new ArrayList<T>());
        for (final List<? extends T> delegate : delegates) {
            if (delegate == null) {
                continue;
            }
            for (final T that : delegate) {
                this.delegate.add(that == null ? null : that.copy());
            }
        }
    }
}
