package io.machinecode.chainlink.spi.jsl.util;

import io.machinecode.chainlink.spi.Copyable;
import io.machinecode.chainlink.spi.Inheritable;
import io.machinecode.chainlink.spi.InheritableElement;
import io.machinecode.chainlink.spi.loader.JobRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class InheritList<T extends Inheritable<T> & Copyable<T>> extends ForwardingList<T> {
    private static final long serialVersionUID = 1L;

    public InheritList(final JobRepository repository, final String defaultJobXml, final List<? extends T> delegate) {
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
