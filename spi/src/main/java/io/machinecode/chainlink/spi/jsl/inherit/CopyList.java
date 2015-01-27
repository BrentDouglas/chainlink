package io.machinecode.chainlink.spi.jsl.inherit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class CopyList<T extends Copyable<T>> extends ForwardingList<T> {
    private static final long serialVersionUID = 1L;

    public CopyList(final List<? extends T> delegate) {
        super(delegate == null ? Collections.<T>emptyList() : new ArrayList<T>(delegate.size()));
        if (delegate == null) {
            return;
        }
        for (final T that : delegate) {
            this.delegate.add(that == null ? null : that.copy());
        }
    }

    public CopyList(final List<? extends T> first, final List<? extends T> second) {
        super(new ArrayList<T>(2));
        if (first != null) {
            for (final T that : first) {
                this.delegate.add(that == null ? null : that.copy());
            }
        }
        if (second != null) {
            for (final T that : second) {
                this.delegate.add(that == null ? null : that.copy());
            }
        }
    }
}
