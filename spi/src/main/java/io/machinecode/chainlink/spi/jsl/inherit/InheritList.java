package io.machinecode.chainlink.spi.jsl.inherit;

import io.machinecode.chainlink.spi.loader.InheritableJobLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class InheritList<T extends Inheritable<T> & Copyable<T>> extends ForwardingList<T> {
    private static final long serialVersionUID = 1L;

    public InheritList(final InheritableJobLoader repository, final String defaultJobXml, final List<? extends T> delegate) {
        super(new ArrayList<T>(delegate.size()));
        for (final T that : delegate) {
            if (that instanceof InheritableElement) {
                final InheritableElement inheritable = (InheritableElement)that;
                if (inheritable.isAbstract() != null && inheritable.isAbstract()) {
                    continue;
                }
            }
            this.delegate.add(that.inherit(repository, defaultJobXml));
        }
    }
}
