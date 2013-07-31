package io.machinecode.nock.jsl.xml.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CopyList<T extends Copyable> extends ForwardingList<T> implements List<T> {

    public CopyList(final List<T> delegate) {
        super(new ArrayList<T>(delegate.size()));
        for (final T that : delegate) {
            this.delegate.add(that == null ? null : (T) that.copy());
        }
    }

    public CopyList(final List<T>... delegates) {
        super(new ArrayList<T>());
        for (final List<T> delegate : delegates) {
            if (delegate == null) {
                continue;
            }
            for (final T that : delegate) {
                this.delegate.add(that == null ? null : (T) that.copy());
            }
        }
    }
}
